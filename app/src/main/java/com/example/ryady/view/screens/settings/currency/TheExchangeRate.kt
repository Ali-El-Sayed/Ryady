package com.example.ryady.view.screens.settings.currency

import android.annotation.SuppressLint
import android.content.Context
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Currency
import com.example.ryady.utils.readCurrency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
object TheExchangeRate {
    lateinit var currency: Currency
    lateinit var chosenCurrency: Pair<String, String>
    private lateinit var passedScope: CoroutineScope
    private lateinit var remoteDataSource: IRemoteDataSource
    private lateinit var context: Context

    private var _currencyInfo = MutableStateFlow(0)
    val currencyInfo: StateFlow<Int> = _currencyInfo

    fun initialize(context: Context, remoteDataSource: IRemoteDataSource, passedScope: CoroutineScope) {
        this.remoteDataSource = remoteDataSource
        this.context = context
        this.passedScope = passedScope
        getRate()
        getSavedRate()
    }

    private fun getRate() {
        passedScope.launch {
            remoteDataSource.fetchExchangeRates().collectLatest {
                currency = it.body()!!
                _currencyInfo.value = 1
            }
        }
    }

    fun getSavedRate() {
        passedScope.launch {
            readCurrency(context) {
                chosenCurrency = Pair(it.get("currency code")!!, it.get("currency name")!!)
            }
        }
    }


}


