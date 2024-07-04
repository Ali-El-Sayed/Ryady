package com.example.ryady.model

import com.google.gson.annotations.SerializedName


data class Currency(

    @SerializedName("date") var date: String = "2024-06-20 00:00:00+00",
    @SerializedName("base") var base: String = "USD",
    @SerializedName("rates") var rates: HashMap<String, Double> = hashMapOf(
        "EGP" to 48.01, "USD" to 1.0, "EUR" to 0.91
    )

)