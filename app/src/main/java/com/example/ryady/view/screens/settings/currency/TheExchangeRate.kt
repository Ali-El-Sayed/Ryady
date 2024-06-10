package com.example.ryady.view.screens.settings.currency

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.RetrieveCartQuery
import com.example.payment.State
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Currency
import com.example.ryady.network.model.Response
import com.example.ryady.utils.readCurrency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
object TheExchangeRate {
    lateinit var currency: Currency
    lateinit var choosedCurrency: Pair<String, String>
    private lateinit var passedScope: CoroutineScope
    private lateinit var remoteDataSource: IRemoteDataSource
    private lateinit var context: Context

    private var _currencyInfo = MutableStateFlow<Int>(0)
    val currencyInfo: StateFlow<Int> = _currencyInfo

    fun initialize(context: Context,remoteDataSource: IRemoteDataSource,passedScope: CoroutineScope) {
        this.remoteDataSource = remoteDataSource
        this.context=context
        this.passedScope=passedScope
        Log.d("Currency", "here i am")
        getRate()
        getSavedRate()
    }

    private fun getRate() {
        passedScope.launch {
            remoteDataSource.fetchExchangeRates().collectLatest {
                Log.d("Currency", "getRate: "+it.body().toString())
                Log.d("Currency", "getRate: "+it.errorBody())

                currency = it.body()!!
                _currencyInfo.value = 1
            }
        }
    }
     fun getSavedRate() {
         passedScope.launch {
            readCurrency(context){
                choosedCurrency = Pair(it.get("currency code")!!,it.get("currency name")!!)
            }
        }
    }


}


