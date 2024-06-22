package com.example.ryady.view.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.view.screens.auth.viewModel.LoginViewModel
import com.example.ryady.view.screens.cart.viewModel.CartViewModel
import com.example.ryady.view.screens.favorite.ViewModel.FavouriteViewModel
import com.example.ryady.view.screens.home.viewmodel.HomeViewModel
import com.example.ryady.view.screens.product.viewModel.ProductViewModel
import com.example.ryady.view.screens.productsByBrand.viewmodel.ProductsViewmodel
import com.example.ryady.view.screens.search.viewModel.SearchViewModel
import com.example.ryady.view.screens.settings.address.viewModel.AddressViewModel
import com.example.ryady.view.screens.settings.countries.viewmodel.CountriesViewModel
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import com.example.ryady.view.screens.settings.currency.viewmodel.CurrencyViewModel
import com.example.ryady.view.screens.settings.orders.viewModel.OrdersViewModel
import com.example.ryady.view.screens.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val remote: IRemoteDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(remote) as T
            modelClass.isAssignableFrom(ProductsViewmodel::class.java) -> ProductsViewmodel(
                remote,
                TheExchangeRate.currency,
                TheExchangeRate.chosenCurrency
            ) as T

            modelClass.isAssignableFrom(ProductViewModel::class.java) -> ProductViewModel(remote) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(remote) as T
            modelClass.isAssignableFrom(CartViewModel::class.java) -> CartViewModel(
                remote,
                Dispatchers.IO
            ) as T

            modelClass.isAssignableFrom(FavouriteViewModel::class.java) -> FavouriteViewModel(remote) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(remote) as T
            modelClass.isAssignableFrom(CountriesViewModel::class.java) -> CountriesViewModel(remote) as T
            modelClass.isAssignableFrom(CurrencyViewModel::class.java) -> CurrencyViewModel(remote) as T
            modelClass.isAssignableFrom(AddressViewModel::class.java) -> AddressViewModel(remote) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(remote) as T
            modelClass.isAssignableFrom(OrdersViewModel::class.java) -> OrdersViewModel(remote) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}