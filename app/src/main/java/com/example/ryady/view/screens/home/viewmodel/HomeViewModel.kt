package com.example.ryady.view.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.type.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val remoteDataSource: IRemoteDataSource) : ViewModel() {
    private var products: MutableStateFlow<ArrayList<Product>> = MutableStateFlow(arrayListOf())
    val productList = products.asStateFlow()

    suspend fun fetchProducts() {
        viewModelScope.launch {
//            products = remoteDataSource.fetchProducts<ArrayList<Product>>()
        }
    }

}

