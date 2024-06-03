package com.example.payment

import android.text.Html
import androidx.core.text.HtmlCompat
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.security.PublicKey

interface PaymentService {

    @POST("v1/intention/")
    suspend fun createPayment(@Body paymentRequest: PaymentRequest, @Header("Authorization") authToken: String="Token egy_sk_test_0fdbdc99012c49651978f2e19e987e3e853d356a332d16763aaef916eaa2cd7d"): Response<PaymentCreationResult>

    @GET("unifiedcheckout/")
    suspend fun getPaymentPage(
        @Query("publicKey") publicKey: String,
        @Query("clientSecret") clientSecret: String,
    ): Response<ResponseBody>
}