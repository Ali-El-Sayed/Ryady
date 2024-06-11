package com.example.ryady.view.screens.auth.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SingUpFragment : Fragment() {

    private val binding: FragmentSingUpBinding by lazy {
        FragmentSingUpBinding.inflate(
            layoutInflater
        )
    }
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
        binding.btnSignUp.setOnClickListener {
            if (checkIsEmpty()) {
                showErrorMessage()
            } else {
                removeErrorMessage()
                createCustomerData()
                viewModel.createAccountFirebase(customer)
                showVerificationAlert(customer)
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
                            Toast.makeText(requireContext(), "verfiy your account", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.createCartState.collectLatest {
                when (it) {
                    is Response.Error -> {

                    }

                    is Response.Loading -> {

                    }

                    is Response.Success -> {
                        saveCart(requireContext(), it.data.first, it.data.second)
                        // save to firebase
                        val database = FirebaseDatabase.getInstance("https://ryady-bf500-default-rtdb.europe-west1.firebasedatabase.app/")
                        val customerRef = database.getReference("CustomerCart")
                        val customerCartData = CustomerCartData(it.data.first, it.data.second)

// Encode the email
                        val encodedEmail = encodeEmail(email)

// Save the data to the database
                        customerRef.child(encodedEmail).setValue(customerCartData)
                            .addOnSuccessListener {
                                println("Data saved successfully")
                            }
                            .addOnFailureListener {
                                println("Error saving data: ${it.message}")
                            }

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
            binding.tilEmail.error = "Please Complete your Email"
        } else {
            binding.tilEmail.isErrorEnabled = false
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


    private fun showVerificationAlert(customer: CustomerCreateInput) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Verify Account")
            .setMessage("Please go to your Email and verify your Account after that Click Verified")
            .setBackground(
                ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.delete_dialog_background,
                    requireContext().theme
                )
            )
            .setPositiveButton("Verified") { dialog, _ ->
                viewModel.checkVerification(customer) {
                    if (it) {
                        viewModel.createAccount(customer)

                    } else {
                        showVerificationAlert(customer)
                        Snackbar.make(
                            requireView(),
                            "Please Verify Your Account and try Again",
                            Snackbar.ANIMATION_MODE_SLIDE
                        )
                            .show()
                    }
                }

            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()

            }.show()
    }

}