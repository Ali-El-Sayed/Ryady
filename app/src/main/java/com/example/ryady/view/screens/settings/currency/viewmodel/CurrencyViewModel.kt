package com.example.ryady.view.screens.settings.currency.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Currency
import com.example.ryady.model.Symbols
import com.example.ryady.network.model.Response
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CurrencyViewModel(
    private val remoteDataSource: IRemoteDataSource,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private var _currenciesInfo: MutableStateFlow<Response<Symbols>> =
        MutableStateFlow(Response.Loading())
    var currenciesInfo: StateFlow<Response<Symbols>> = _currenciesInfo
    private var _exchangeInfo: MutableStateFlow<Response<Currency>> =
        MutableStateFlow(Response.Loading())
    var exchangeInfo: StateFlow<Response<Currency>> = _exchangeInfo

    suspend fun getCurrencies() {
        viewModelScope.launch(dispatcher) {
            remoteDataSource.fetchAllCurrencies().collectLatest {
                if (it.isSuccessful) {
                    _currenciesInfo.value = Response.Success(it.body()!!)
                }
            }
        }

    }

    suspend fun getExchange() {
        viewModelScope.launch(dispatcher) {
            remoteDataSource.fetchExchangeRates().collectLatest {
                if (it.isSuccessful) {
                    _exchangeInfo.value = Response.Success(it.body()!!)
                }
            }
        }

    }
}