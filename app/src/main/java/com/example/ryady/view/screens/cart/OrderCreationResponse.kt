package com.example.ryady.view.screens.cart

import com.google.gson.annotations.SerializedName


data class OrderCreationResponse(

    @SerializedName("order") var order: Order? = Order()

)
