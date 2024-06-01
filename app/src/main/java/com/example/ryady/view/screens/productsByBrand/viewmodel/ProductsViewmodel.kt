package com.example.ryady.view.screens.productsByBrand.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Product
import com.example.ryady.network.model.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "ProductsViewmodel"
class ProductsViewmodel(private val remoteDataSource: IRemoteDataSource) : ViewModel() {
    private var products: MutableStateFlow<Response<ArrayList<Product>>> = MutableStateFlow(Response.Loading())
    val productList = products.asStateFlow()


    suspend fun getProductsByBrandId(id: String) {
        val response = remoteDataSource.fetchProductsByBrandId<Response<ArrayList<Product>>>(id)
        when (response) {
            is Response.Loading -> products.value = Response.Loading()
            is Response.Success -> products.value = Response.Success(response.data as ArrayList<Product>)
            is Response.Error -> products.value = Response.Error(response.message)
        }
    }

    suspend fun getProductsByCategory(categoryType: CategoryType) {
        products.value = Response.Loading()
        Log.d(TAG, "getProductsByCategory: ${categoryType.category}")
        val response = remoteDataSource.fetchProductsByCategory<Response<java.util.ArrayList<Product>>>(categoryType.category)
        when (response) {
            is Response.Loading -> products.value = Response.Loading()
            is Response.Success -> products.value = Response.Success(response.data as ArrayList<Product>)
            is Response.Error -> products.value = Response.Error(response.message)
        }
    }
}

enum class CategoryType( val category: String) {
    ACCESSORIES("product_type:ACCESSORIES"), SHOES("product_type:SHOES"), T_SHIRTS("product_type:T-SHIRTS"), ALL("")
}