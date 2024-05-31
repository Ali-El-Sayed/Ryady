package com.example.ryady.view.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.product.viewModel.ProductViewModel
import com.example.ryady.view.screens.home.viewmodel.HomeViewModel
import com.example.ryady.view.screens.productsByBrand.viewmodel.ProductsViewmodel

class ViewModelFactory(private val remote: IRemoteDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(remote) as T
            modelClass.isAssignableFrom(ProductsViewmodel::class.java) -> ProductsViewmodel(remote) as T
            modelClass.isAssignableFrom(ProductViewModel::class.java) -> ProductViewModel(remote) as T

            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}