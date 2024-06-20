package com.example.ryady.view.screens.settings.orders.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.ryady.databinding.FragmentOrderDetailsDialogBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.model.extensions.roundTo2DecimalPlaces
import com.example.ryady.network.GraphqlClient
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import com.example.ryady.view.screens.settings.orders.adapter.OrderDetailsAdapter
import com.example.ryady.view.screens.settings.orders.viewModel.OrdersViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.wasabeef.recyclerview.animators.LandingAnimator

class OrderDetailsDialog : BottomSheetDialogFragment() {
    private val binding: FragmentOrderDetailsDialogBinding by lazy { FragmentOrderDetailsDialogBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        val factory = ViewModelFactory(
            RemoteDataSource.getInstance(
                GraphqlClient.apiService
            )
        )
        ViewModelProvider(requireParentFragment(), factory)[OrdersViewModel::class.java]
    }
    private lateinit var adapter: OrderDetailsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRecyclerView()
        adapter.submitList(viewModel.selectedOrder.items)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.orderId.text = viewModel.selectedOrder.orderName

        binding.itemsNumber.text = "$${viewModel.selectedOrder.items.size} Items"
        val total =
            (viewModel.selectedOrder.totalPrice.toDouble() / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                TheExchangeRate.chosenCurrency.first
            )!!)).roundTo2DecimalPlaces()
        binding.totalPrice.text = "$$total ${TheExchangeRate.chosenCurrency.first}"
        binding.closeBtn.setOnClickListener { dismiss() }
    }

    private fun setupRecyclerView() {
        adapter = OrderDetailsAdapter()
        binding.orderItemsList.adapter = adapter
        binding.orderItemsList.itemAnimator = LandingAnimator()
    }

}