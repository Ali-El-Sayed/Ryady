package com.example.ryady.view.screens.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ryady.R
import com.example.ryady.databinding.FragmentCustomerDataBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.model.PaymentMethod
import com.example.ryady.network.GraphqlClient
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.order.viewmodel.OrderViewModel
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem


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
        binding.spinner.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem(
                        text = context.getString(R.string.cash_on_delivery), iconRes = R.drawable.ic_cash_on_delivery
                    ), IconSpinnerItem(text = context.getString(R.string.credit_card), iconRes = R.drawable.ic_credit_card)
                )
            )
            getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 2)
            selectItemByIndex(1) // select a default item.
            lifecycleOwner = lifecycleOwner
        }
        binding.spinner.setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, newIndex, item ->
            when (newIndex) {
                // Cash On Delivery
                0 -> viewModel.currentOrder.paymentMethod = PaymentMethod.CASH_ON_DELIVERY
                // Credit Card
                1 -> viewModel.currentOrder.paymentMethod = PaymentMethod.CREDIT_CARD
            }
        }
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