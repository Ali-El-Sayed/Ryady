package com.example.ryady.view.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Product
import com.example.ryady.network.model.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class HomeViewModel(private val remoteDataSource: IRemoteDataSource) : ViewModel() {
    private var products: MutableStateFlow<Response<ArrayList<Product>>> =
        MutableStateFlow(Response.Loading())
    val productList = products.asStateFlow()

    init {
        viewModelScope.launch { fetchProducts() }
    }

    suspend fun fetchProducts() {
        withTimeout(5000) {
            val response = remoteDataSource.fetchProducts<ArrayList<Product>>()

            when (response) {
                is Response.Loading -> products.value = Response.Loading()
                is Response.Success -> products.value = Response.Success(response.data)
                is Response.Error -> products.value = Response.Error(response.message)
            }
        }
    }
}
