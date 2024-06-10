package com.example.ryady.model

import com.google.gson.annotations.SerializedName


data class Symbols (

    @SerializedName("currencySymbols" ) var currencySymbols :  HashMap<String,String>? = null

)