package com.example.ryady.view.screens.product.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ProductByIdQuery
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.network.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "ProductViewModel"

class ProductViewModel(private val remoteDataSource: IRemoteDataSource) : ViewModel() {
var cartId = ""

    private var _productInfo: MutableStateFlow<Response<ProductByIdQuery.Product>> =
        MutableStateFlow(Response.Loading())
    var productInfo: StateFlow<Response<ProductByIdQuery.Product>> = _productInfo
    private var _addItemToCartInfo: MutableStateFlow<Response<Int>> =
        MutableStateFlow(Response.Loading())
    var addItemToCartInfo: StateFlow<Response<Int>> = _addItemToCartInfo

     fun fetchProductById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.fetchProductById(id = id)
                .collectLatest {
                    _productInfo.value = it
                }
        }

    }

     fun addItemToCart(
        cartId: String,
        varientID: String,
        quantity: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = remoteDataSource.addItemToCart<Int>(
                cartId = cartId,
                varientID = varientID,
                quantity = quantity
            )
            when (response) {
                is Response.Error -> _addItemToCartInfo.value = Response.Error(response.message)
                is Response.Loading -> _addItemToCartInfo.value = Response.Loading()
                is Response.Success -> _addItemToCartInfo.value = Response.Success(response.data)
            }
        }

    }

    fun addItemToFav(email: String,product: ProductByIdQuery.Product) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.addItemToFavourite(email = email, product = product)
        }
    }

    fun searchForAnItem(email: String,itemId: String, isFound: (found: Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.searchForAnItem(email = email, itemId = itemId, isFound = isFound)
        }
    }

    fun deleteItem(email: String,id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.deleteItem(email = email , itemId = id)
        }
    }
}