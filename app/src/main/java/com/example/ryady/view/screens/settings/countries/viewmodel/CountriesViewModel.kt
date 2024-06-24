package com.example.ryady.view.screens.settings.countries.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.network.model.Response
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CountriesViewModel(
    private val remoteDataSource: IRemoteDataSource,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private var _countriesInfo: MutableStateFlow<Response<HashMap<String, String>>> =
        MutableStateFlow(Response.Loading())
    var countriesInfo: StateFlow<Response<HashMap<String, String>>> = _countriesInfo

    suspend fun getCountries() {
        viewModelScope.launch {
            remoteDataSource.fetchCountries().collectLatest {
                if (it.isSuccessful) {
                    _countriesInfo.value = Response.Success(it.body() ?: HashMap<String, String>())
                }
            }
        }
    }

}