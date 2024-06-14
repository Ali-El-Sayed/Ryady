package com.example.ryady.view.screens.search.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.SearchProductsQuery
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.network.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel(val remoteDataSource: IRemoteDataSource) : ViewModel() {
    private val _searchProduct: MutableStateFlow<Response<List<SearchProductsQuery.Edge>>> =
        MutableStateFlow(Response.Loading())
    val searchProduct: StateFlow<Response<List<SearchProductsQuery.Edge>>> = _searchProduct


    fun getSearchedItem(itemTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.searchForProducts<List<SearchProductsQuery.Edge>>(itemTitle)
                .collectLatest {response ->
                    _searchProduct.value = response
                }
        }
    }
}