package com.example.ryady.view.screens.auth.view

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.api.Optional
import com.example.GetCustomerDataQuery
import com.example.ryady.R
import com.example.ryady.databinding.FragmentSingUpBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.datasource.remote.util.RemoteDSUtils.encodeEmail
import com.example.ryady.model.CustomerCartData
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.utils.saveCart
import com.example.ryady.utils.saveUserData
import com.example.ryady.view.extensions.move
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.auth.viewModel.LoginViewModel
import com.example.ryady.view.screens.home.MainActivity
import com.example.type.CustomerAccessTokenCreateInput
import com.example.type.CustomerCreateInput
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "SingUpFragment"

class SingUpFragment : Fragment() {

    private val binding: FragmentSingUpBinding by lazy {
        FragmentSingUpBinding.inflate(
            layoutInflater
        )
    }

    private lateinit var dialog: Dialog
    private lateinit var dialogBtnVerification: Button
    private lateinit var dialogBtnCancel: Button
    private var email = ""
    private val viewModel: LoginViewModel by lazy {
        val factory =
            ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }
    private lateinit var customer: CustomerCreateInput

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeVerificationDialog()
        binding.btnSignUp.setOnClickListener {
            if (checkIsEmpty()) {
                showErrorMessage()
            } else {
                if (isValidEmail(binding.etEmail.text.toString())) {
                    removeErrorMessage()
                    createCustomerData()
                    binding.frameLayout.root.visibility = View.VISIBLE
                    viewModel.createAccountFirebase(customer)
                    showVerificationAlert()
                } else {
                    showErrorMessage()
                }

            }


        }

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.createdAccount.collect { account ->
                withContext(Dispatchers.IO) {
                    when (account) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            email = account.data.email ?: ""
                            viewModel.loginToAccount(
                                CustomerAccessTokenCreateInput(
                                    account.data.email.toString(),
                                    binding.etPassword.text.toString()
                                )
                            )

                            viewModel.loginAccountState.collectLatest { token ->
                                when (token) {
                                    is Response.Error -> {
                                        Snackbar.make(
                                            binding.root,
                                            "error get token from server",
                                            Snackbar.ANIMATION_MODE_SLIDE
                                        ).show()
                                    }

                                    is Response.Loading -> {

                                    }

                                    is Response.Success -> {
                                        viewModel.createEmptyCart(
                                            account.data.email.toString(),
                                            token.data
                                        )
                                        saveUserData(
                                            requireContext(),
                                            customer = GetCustomerDataQuery.Customer(
                                                email = account.data.email,
                                                firstName = account.data.firstName,
                                                lastName = account.data.lastName,
                                                id = account.data.id,
                                                phone = "",
                                                displayName = account.data.displayName,
                                                acceptsMarketing = account.data.acceptsMarketing
                                            ),
                                            customerToken = token.data
                                        )

                                        withContext(Dispatchers.Main) {
                                            requireActivity().move(
                                                requireContext(),
                                                MainActivity::class.java
                                            )
                                            requireActivity().finish()
                                        }
                                    }
                                }
                            }

                        }

                        is Response.Error -> {
                            withContext(Dispatchers.Main) {

                                Snackbar.make(
                                    binding.root,
                                    "Please Verify Your Account ",
                                    Snackbar.ANIMATION_MODE_SLIDE
                                ).show()

                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.createCartState.collectLatest {
                when (it) {
                    is Response.Error -> {
                        Snackbar.make(binding.root, it.message, Snackbar.ANIMATION_MODE_SLIDE)
                            .show()
                    }

                    is Response.Loading -> {

                    }

                    is Response.Success -> {
                        saveCart(requireContext(), it.data.first, it.data.second)
                        val database =
                            FirebaseDatabase.getInstance("https://ryady-bf500-default-rtdb.europe-west1.firebasedatabase.app/")
                        val customerRef = database.getReference("CustomerCart")
                        val customerCartData = CustomerCartData(it.data.first, it.data.second)
                        val encodedEmail = encodeEmail(email)
                        customerRef.child(encodedEmail).setValue(customerCartData)
                    }
                }
            }
        }

    }

    private fun removeErrorMessage() {
        binding.tilEmail.isErrorEnabled = false
        binding.tilFirstName.isErrorEnabled = false
        binding.tilLastName.isErrorEnabled = false
        binding.tilPassword.isErrorEnabled = false
    }

    private fun createCustomerData() {
        customer = CustomerCreateInput(
            email = binding.etEmail.text.toString(),
            password = binding.etPassword.text.toString(),
            firstName = Optional.present(binding.etFirstName.text.toString()),
            lastName = Optional.present(binding.etLastName.text.toString())
        )
    }

    private fun checkIsEmpty(): Boolean {
        return binding.etEmail.text.isNullOrEmpty() || binding.etFirstName.text.isNullOrEmpty() || binding.etLastName.text.isNullOrEmpty() || binding.etPassword.text.isNullOrEmpty()
    }


    private fun showErrorMessage() {
        if (binding.etEmail.text.isNullOrEmpty()) {
            Log.i(TAG, "showErrorMessage: not valid")
            binding.tilEmail.error = "Please Complete your Email"
        } else {
            Log.i(TAG, "showErrorMessage: test else")
            if (isValidEmail(binding.etEmail.text.toString())) {
                binding.tilEmail.isErrorEnabled = false
            } else {
                binding.tilEmail.error = "Please Enter Valid Email"
            }
        }

        if (binding.etFirstName.text.isNullOrEmpty()) {
            binding.tilFirstName.error = "Please Complete your First Name"
        } else {
            binding.tilFirstName.isErrorEnabled = false
        }
        if (binding.etLastName.text.isNullOrEmpty()) {
            binding.tilLastName.error = "Please Complete your Last Name"
        } else {
            binding.tilLastName.isErrorEnabled = false
        }
        if (binding.etPassword.text.isNullOrEmpty()) {
            binding.tilPassword.error = "Please Complete your Last Name"
        } else {
            binding.tilPassword.isErrorEnabled = false
        }

    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        Log.i(TAG, "isValidEmail: ${emailRegex.matches(email)}")
        return emailRegex.matches(email)
    }

    private fun initializeVerificationDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.verification_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(requireContext().getDrawable(R.drawable.verification_dialog_background))
        dialog.setCancelable(false)

        dialogBtnVerification = dialog.findViewById(R.id.btn_verification)
        dialogBtnCancel = dialog.findViewById(R.id.btn_cancel)

        dialogBtnVerification.setOnClickListener {
            viewModel.checkVerification(customer) {
                binding.frameLayout.root.visibility = View.VISIBLE
                if (it) {
                    viewModel.createAccount(customer)
                    dialog.dismiss()
                } else {
                    showVerificationAlert()
                    Snackbar.make(
                        requireView(),
                        "Please Verify Your Account and try Again",
                        Snackbar.ANIMATION_MODE_SLIDE
                    ).show()
                }
            }
        }
        dialogBtnCancel.setOnClickListener {
            dialog.dismiss()

        }
    }

    private fun showVerificationAlert() {
        binding.frameLayout.root.visibility = View.GONE
        dialog.show()
    }

}