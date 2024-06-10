package com.example.ryady.view.screens.settings.currency

import com.example.ryady.model.Currency
import com.example.ryady.model.Symbols
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {

    @GET("v2.0/rates/latest")
    suspend fun getExchangerate(
        @Query("apikey") apiKey: String = "0afb7b270c1c42a2b2c1a4656585dbc7"
    ): Response<Currency>

    @GET("v2.0/currency-symbols")
    suspend fun getAllCurrencies():Response<Symbols>

}