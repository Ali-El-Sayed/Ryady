package com.example.ryady.view.screens.cart

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OrderService {
    @POST("orders.json")
    suspend fun createOrder(
        @Body orderRequest: OrderRequest,
        @Header("X-Shopify-Access-Token") authToken: String = "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"
    ): Response<OrderCreationResponse>

}