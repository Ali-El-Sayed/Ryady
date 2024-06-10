package com.example.ryady.view.screens.Favourite.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.model.Product
import com.example.ryady.network.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouriteViewModel(private val remoteDataSource: IRemoteDataSource) : ViewModel() {

    private val _productList: MutableStateFlow<Response<List<Product>>> =
        MutableStateFlow(Response.Loading())
    var productList: StateFlow<Response<List<Product>>> = _productList


    fun getAllFavouriteProduct(email : String) {
        viewModelScope.launch(Dispatchers.IO) {


            remoteDataSource.getAllFavouriteItem(email) {

                _productList.value = Response.Success(it)
            }
        }
    }

    fun deleteItem(email: String , itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {

            remoteDataSource.deleteItem(email =  email,itemId)
        }
    }

}