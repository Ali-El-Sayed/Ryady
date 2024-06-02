package com.example.ryady.login.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.ryady.databinding.FragmentLoginBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.login.viewModel.LoginViewModel
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.type.CustomerAccessTokenCreateInput
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "LoginFragment"

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var customerInput: CustomerAccessTokenCreateInput
    private val viewModel: LoginViewModel by lazy {
        val factory =
            ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

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
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.loginToAccount(customerInput)

                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.loginAccountState.collect {
                Log.i(TAG, "onViewCreated: $it")
                when (it) {
                    is Response.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Email Or Password not correct",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is Response.Loading -> {

                    }

                    is Response.Success -> {

                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeScreen())


                    }
                }

            }
        }
        binding.tvSignUp.setOnClickListener {
            Log.i(TAG, "onViewCreated: Click Sing")
            Navigation.findNavController(binding.root).navigate(LoginFragmentDirections.actionLoginFragmentToSingUpFragment())
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