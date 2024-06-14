package com.example.ryady.view.screens.settings.orders.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ryady.databinding.FragmentOrdersBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.utils.readCustomerData
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import com.example.ryady.view.screens.settings.orders.adapter.OrdersAdapter
import com.example.ryady.view.screens.settings.orders.viewModel.OrdersViewModel
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "OrdersFragment"

class OrdersFragment : Fragment() {

    private val binding by lazy { FragmentOrdersBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        val factory = ViewModelFactory(
            RemoteDataSource.getInstance(
                GraphqlClient.apiService
            )
        )
        ViewModelProvider(this, factory)[OrdersViewModel::class.java]
    }
    private lateinit var adapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            launch {
                readCustomerData(requireContext()) {
                    viewModel.userToken = it["user token"] ?: ""
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchOrders()

            }
        }
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.orders.collectLatest {
                    when (it) {
                        is Response.Error -> {}
                        is Response.Loading -> toggleLoadingIndicator()
                        is Response.Success -> {
                            TheExchangeRate.currencyInfo.collectLatest { ex ->
                                if (ex == 1) withContext(Dispatchers.Main) {
                                    adapter.submitList(it.data)
                                    toggleLoadingIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = OrdersAdapter { order ->
            viewModel.selectedOrder = order
            val orderDetailsDialog = OrderDetailsDialog()
            orderDetailsDialog.isCancelable = false
            orderDetailsDialog.show(childFragmentManager, "offlineDialogFragment")
        }
        binding.orderList.adapter = adapter
        binding.orderList.itemAnimator = LandingAnimator()
    }

    private fun toggleLoadingIndicator() {
        binding.frameLayout.visibility = if (binding.frameLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }
}