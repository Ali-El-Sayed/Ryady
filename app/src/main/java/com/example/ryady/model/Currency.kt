package com.example.ryady.model

import com.google.gson.annotations.SerializedName


data class Currency (

    @SerializedName("date"  ) var date  : String? = null,
    @SerializedName("base"  ) var base  : String? = null,
    @SerializedName("rates" ) var rates : HashMap<String,Double>? = null

)