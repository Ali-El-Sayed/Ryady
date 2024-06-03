package com.example.ryady.view.screens.product.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ProductByIdQuery
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "ProductViewModel"
class ProductViewModel(private val remoteDataSource: IRemoteDataSource) :ViewModel() {


    private var _productInfo: MutableStateFlow<Response<ProductByIdQuery.Product>> = MutableStateFlow(Response.Loading())
    var productInfo : StateFlow<Response<ProductByIdQuery.Product>>  = _productInfo

     suspend fun fetchProductById(id : String){
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.fetchProductById(id = id)
                .collectLatest {
                _productInfo.value = it
            }
        }

    }
}