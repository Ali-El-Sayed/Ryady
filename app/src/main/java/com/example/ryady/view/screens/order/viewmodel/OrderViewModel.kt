package com.example.ryady.view.screens.order.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Order

class OrderViewModel(private val remote: IRemoteDataSource) : ViewModel() {
    var currentOrder: Order = Order()

}