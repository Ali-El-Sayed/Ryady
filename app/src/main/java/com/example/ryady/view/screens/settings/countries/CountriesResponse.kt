package com.example.ryady.view.screens.settings.countries

sealed
class CountriesResponse {
    class Success(val data: HashMap<String,String>) : CountriesResponse()
    class Failure(val msg:Throwable) : CountriesResponse()
    object Loading : CountriesResponse()
}