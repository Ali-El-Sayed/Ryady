package com.example.ryady.view.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Brand
import com.example.ryady.model.Product
import com.example.ryady.network.model.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel(private val remoteDataSource: IRemoteDataSource) : ViewModel() {
    private var products: MutableStateFlow<Response<ArrayList<Product>>> = MutableStateFlow(Response.Loading())
    val productList = products.asStateFlow()

    private var brands: MutableStateFlow<Response<ArrayList<Brand>>> = MutableStateFlow(Response.Loading())
    val brandList = brands.asStateFlow()

    init {
        viewModelScope.launch {
            fetchProducts()
            fetchBrands()
            joinAll()
        }
    }

    private suspend fun fetchProducts() {
        products.value = Response.Loading()
        val response = remoteDataSource.fetchProducts<ArrayList<Product>>()
        when (response) {
            is Response.Loading -> products.value = Response.Loading()
            is Response.Success -> products.value = Response.Success(response.data)
            is Response.Error -> products.value = Response.Error(response.message)
        }
    }

    private suspend fun fetchBrands() {
        brands.value = Response.Loading()
        val response = remoteDataSource.fetchBrands<ArrayList<Brand>>()
        when (response) {
            is Response.Loading -> brands.value = Response.Loading()
            is Response.Success -> brands.value = Response.Success(response.data)
            is Response.Error -> brands.value = Response.Error(response.message)
        }
    }
}
