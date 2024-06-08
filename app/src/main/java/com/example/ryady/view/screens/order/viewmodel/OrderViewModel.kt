package com.example.ryady.view.screens.order.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Order
import kotlinx.coroutines.launch

class OrderViewModel(private val remote: IRemoteDataSource) : ViewModel() {
    var currentOrder: Order = Order()


    suspend fun createOrderInformation() {
        viewModelScope.launch {
            remote.createOrderInformation(
                "f4093054bf8cf9c70e84961dd8a27ed3",
                currentOrder
            )
        }
    }
}