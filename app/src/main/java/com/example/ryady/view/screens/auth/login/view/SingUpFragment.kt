package com.example.ryady.view.screens.auth.login.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.api.Optional
import com.example.ryady.BaseActivity
import com.example.ryady.databinding.FragmentSingUpBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.auth.login.viewModel.LoginViewModel
import com.example.ryady.view.screens.home.MainActivity
import com.example.type.CustomerCreateInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SingUpFragment : Fragment() {

    private val binding: FragmentSingUpBinding by lazy { FragmentSingUpBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }
    private lateinit var customer: CustomerCreateInput

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            if (checkIsEmpty()) {
                Toast.makeText(requireContext(), " empty", Toast.LENGTH_LONG).show()
                showErrorMessage()
            } else {
                removeErrorMessage()
                createCustomerData()
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.createAccount(customer)
                }
                Toast.makeText(requireContext(), " no empty", Toast.LENGTH_LONG).show()
            }

        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.createdAccount.collect { account ->
                withContext(Dispatchers.Main) {
                    when (account) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            (requireActivity() as BaseActivity).move(requireContext(), MainActivity::class.java)
                            requireActivity().finish()
                        }

                        is Response.Error -> {}
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

}