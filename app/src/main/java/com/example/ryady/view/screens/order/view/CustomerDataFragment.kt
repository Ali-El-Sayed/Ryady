package com.example.ryady.view.screens.order.view

import android.os.Bundle
import android.text.Editable.Factory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.decode.BitmapFactoryDecoder
import com.example.ryady.databinding.FragmentCustomerDataBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.utils.readCustomerData
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.cart.viewModel.CartViewModel
import kotlinx.coroutines.launch


class CustomerDataFragment : Fragment() {
    private val binding by lazy { FragmentCustomerDataBinding.inflate(layoutInflater) }

    private val viewModel: CartViewModel by lazy {
        val factory = ViewModelFactory(
            RemoteDataSource.getInstance(
                GraphqlClient.apiService
            )
        )
        ViewModelProvider(requireActivity(), factory)[CartViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            readCustomerData(requireContext()){map ->
                binding.etCustomerEmail.text = Factory.getInstance().newEditable(map["user email"])
                binding.etCustomerFirstName.text = Factory.getInstance().newEditable(map["first name"])
                binding.etCustomerLastName.text = Factory.getInstance().newEditable(map["last name"])
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)/* payment method spinner */


        binding.btnCancel.setOnClickListener { requireActivity().finish() }
        binding.btnNext.setOnClickListener {
            // customer email
            viewModel.currentOrder.customerEmail = binding.etCustomerEmail.text.toString()
            // customer name
            viewModel.currentOrder.customerFirstName = binding.etCustomerFirstName.text.toString()
            viewModel.currentOrder.customerLastName = binding.etCustomerLastName.text.toString()
            // phone number
            viewModel.currentOrder.customerPhoneNumbers = binding.etCustomerPhone.text.toString()
            findNavController().navigate(CustomerDataFragmentDirections.actionCustomerDataFragmentToOrderDetailsFragment())
        }

    }
}