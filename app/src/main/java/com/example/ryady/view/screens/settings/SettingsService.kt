package com.example.ryady.view.screens.settings

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SettingsService {

    @GET("en/codes.json")
    suspend fun getCountries(): Response<HashMap<String, String>>


}