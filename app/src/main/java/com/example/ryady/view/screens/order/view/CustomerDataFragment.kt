package com.example.ryady.view.screens.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ryady.databinding.FragmentCustomerDataBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.order.viewmodel.OrderViewModel


class CustomerDataFragment : Fragment() {
    private val binding by lazy { FragmentCustomerDataBinding.inflate(layoutInflater) }

    private val viewModel: OrderViewModel by lazy {
        val factory = ViewModelFactory(
            RemoteDataSource.getInstance(
                GraphqlClient.apiService
            )
        )
        ViewModelProvider(requireActivity(), factory)[OrderViewModel::class.java]
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
            viewModel.currentOrder.customerName = binding.etCustomerName.text.toString()
            // phone number
            if (binding.etAnotherPhone.text.toString().isNotEmpty()) {
                viewModel.currentOrder.customerPhoneNumbers =
                    "${binding.etAnotherPhone.text.toString()}, ${binding.etAnotherPhone.text.toString()}"
            } else viewModel.currentOrder.customerPhoneNumbers = binding.etCustomerPhone.text.toString()
            findNavController().navigate(CustomerDataFragmentDirections.actionCustomerDataFragmentToOrderDetailsFragment())
        }

    }
}