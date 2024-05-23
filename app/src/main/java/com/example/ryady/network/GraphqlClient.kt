package com.example.ryady.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpHeader

object GraphqlClient {
    private const val BASE_URL = "https://mad44-android-sv-1.myshopify.com/api/2024-04/graphql.json"
    private val headers =
        mutableListOf(
            HttpHeader(
                "X-Shopify-Storefront-Access-Token",
                "69070b78abea88f385ee16fd6b5a4c52",
            ),
            HttpHeader(
                "Content-Type",
                "application/json",
            ),
        )

    val apiService: ApolloClient by lazy {
        ApolloClient.Builder().httpHeaders(headers).serverUrl(BASE_URL).build()
    }
}
