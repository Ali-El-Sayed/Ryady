package com.example.ryady.datasource.remote

import com.apollographql.apollo3.ApolloClient
import com.example.ShopifyProductsQuery
import com.example.ryady.model.Product
import com.example.ryady.model.extensions.toProductList
import com.example.ryady.network.model.Response

interface IRemoteDataSource {
    suspend fun fetchProducts(): Response<ArrayList<Product>>
}

class RemoteDataSource private constructor(private val client: ApolloClient) : IRemoteDataSource {
    companion object {
        @Volatile
        private var instance: IRemoteDataSource? = null

        fun getInstance(client: ApolloClient) =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource(client).also { instance = it }
            }
    }

    override suspend fun fetchProducts(): Response<ArrayList<Product>> {
        client.query(ShopifyProductsQuery()).execute().data?.products?.toProductList()
            ?.let {
                return Response.Success(it)
            }
        return Response.Error("Data Not Found")
    }
}
