package com.example.ryady.datasource.remote

import com.apollographql.apollo3.ApolloClient
import com.example.ShopifyBrandsQuery
import com.example.ShopifyProductsQuery
import com.example.ryady.model.extensions.toBrandsList
import com.example.ryady.model.extensions.toProductList
import com.example.ryady.network.model.Response

private const val TAG = "RemoteDataSource"

interface IRemoteDataSource {
    suspend fun <T> fetchProducts(): Response<T>

    suspend fun <T> fetchBrands(): Response<T>
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

    override suspend fun <T> fetchProducts(): Response<T> {
        client.query(ShopifyProductsQuery()).execute().data?.products?.toProductList()?.let {
            return Response.Success(it as T)
        }
        return Response.Error("Data Not Found")
    }

    override suspend fun <T> fetchBrands(): Response<T> {
        client.query(ShopifyBrandsQuery()).execute().data?.collections?.toBrandsList()?.let {
            return Response.Success(it as T)
        }
        return Response.Error("Data Not Found")
    }
}
