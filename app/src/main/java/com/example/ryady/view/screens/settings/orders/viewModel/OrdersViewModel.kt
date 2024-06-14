package com.example.ryady.view.screens.settings.orders.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Order
import com.example.ryady.network.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(private val remote: IRemoteDataSource, var userToken: String = "") : ViewModel() {
    private val _orders: MutableStateFlow<Response<List<Order>>> = MutableStateFlow(Response.Loading())
    val orders = _orders.asStateFlow()

    var selectedOrder: Order = Order()

    suspend fun fetchOrders() {
        _orders.value = Response.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            if (userToken.isNotEmpty()) _orders.value = remote.fetchOrders(userToken)
            else _orders.value = Response.Error("No token provided")
        }
    }

}