package com.example.ryady.view.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ryady.cart.viewModel.CartViewModel
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.view.screens.Favourite.ViewModel.FavouriteViewModel
import com.example.ryady.view.screens.auth.login.viewModel.LoginViewModel
import com.example.ryady.view.screens.product.viewModel.ProductViewModel
import com.example.ryady.view.screens.home.viewmodel.HomeViewModel
import com.example.ryady.view.screens.productsByBrand.viewmodel.ProductsViewmodel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val remote: IRemoteDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(remote) as T
            modelClass.isAssignableFrom(ProductsViewmodel::class.java) -> ProductsViewmodel(remote) as T
            modelClass.isAssignableFrom(ProductViewModel::class.java) -> ProductViewModel(remote) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(remote) as T
            modelClass.isAssignableFrom(CartViewModel::class.java) -> CartViewModel(remote) as T
            modelClass.isAssignableFrom(FavouriteViewModel::class.java) -> FavouriteViewModel(remote) as T


            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}