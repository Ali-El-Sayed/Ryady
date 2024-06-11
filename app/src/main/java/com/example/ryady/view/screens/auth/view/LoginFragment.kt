package com.example.ryady.view.screens.auth.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ryady.databinding.FragmentLoginBinding
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val TAG = "LoginFragment"

class LoginFragment : Fragment() {

    private val binding: FragmentLoginBinding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    lateinit var customerInput: CustomerAccessTokenCreateInput
    private val viewModel: LoginViewModel by lazy {
        val factory =
            ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnLogin.setOnClickListener {
            if (validateUserInputs()) {
                showErrorMessage()
            } else {
                removeErrorMessage()
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                customerInput = CustomerAccessTokenCreateInput(email, password)
                viewModel.loginToAccount(customerInput)
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.loginAccountState.collect {
                when (it) {
                    is Response.Error -> {
                        Toast.makeText(
                            requireContext(), "Email Or Password not correct", Toast.LENGTH_LONG
                        ).show()
                    }

                    is Response.Loading -> {}

                    is Response.Success -> {
                        viewModel.getUserCustomerData(it.data)
                        viewModel.customerData.collectLatest { customerResponse ->
                            when (customerResponse) {
                                is Response.Error -> {

                                }

                                is Response.Loading -> {

                                }

                                is Response.Success -> {
                                    saveUserData(
                                        context = requireContext(),
                                        customer = customerResponse.data,
                                        customerToken = it.data
                                    )
                                    // retrieve from firebase
                                    // save to data store

                                    val database = FirebaseDatabase.getInstance("https://ryady-bf500-default-rtdb.europe-west1.firebasedatabase.app/")
                                    val customerRef = database.getReference("CustomerCart")
                                    customerRef.child( encodeEmail(customerResponse.data.email ?: "")).addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                val customerCartData = dataSnapshot.getValue(
                                                    CustomerCartData::class.java)
                                                lifecycleScope.launch {
                                                    saveCart(
                                                        requireContext(),
                                                        customerCartData?.cartId ?: "",
                                                        customerCartData?.checkoutUrl ?: ""
                                                    )
                                                }
                                                println("Cart ID: ${customerCartData?.cartId}")
                                                println("Checkout URL: ${customerCartData?.checkoutUrl}")
                                            } else {
                                                println("No data found for the email:")
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            println("Error reading data: ${databaseError.message}")
                                        }
                                    })
                                    withContext(Dispatchers.IO){
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
                }

            }
        }
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSingUpFragment())
        }






    }
    private fun removeErrorMessage() {
        binding.tilEmail.isErrorEnabled = false
        binding.tilPassword.isErrorEnabled = false
    }

    private fun showErrorMessage() {
        if (binding.etEmail.text.isNullOrEmpty()) {
            binding.tilEmail.error = "E-Mail can't be empty"
        } else {
            binding.tilEmail.isErrorEnabled = false
        }
        if (binding.etPassword.text.isNullOrEmpty()) {
            binding.tilPassword.error = "Password can't be empty"
        } else {
            binding.tilPassword.isErrorEnabled = false
        }
    }

    private fun validateUserInputs(): Boolean {
        return binding.etEmail.text.isNullOrEmpty() || binding.etPassword.text.isNullOrEmpty()
    }

}