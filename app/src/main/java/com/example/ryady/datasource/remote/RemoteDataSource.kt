package com.example.ryady.datasource.remote

import com.apollographql.apollo3.ApolloClient
import com.example.ShopifyBrandsByIdQuery
import com.example.ShopifyBrandsQuery
import com.example.ShopifyProductsQuery
import com.example.ryady.model.extensions.toBrandsList
import com.example.ryady.model.extensions.toProductList
import com.example.ryady.network.model.Response

private const val TAG = "RemoteDataSource"

interface IRemoteDataSource {
    suspend fun <T> fetchProducts(): Response<T>

    suspend fun <T> fetchBrands(): Response<T>

    suspend fun <T> fetchProductsByBrandId(id: String): Response<T>
}

@Suppress("UNCHECKED_CAST")
class RemoteDataSource private constructor(private val client: ApolloClient) : IRemoteDataSource {
    companion object {
        @Volatile
        private var instance: IRemoteDataSource? = null

        fun getInstance(client: ApolloClient) = instance ?: synchronized(this) {
            instance ?: RemoteDataSource(client).also { instance = it }
        }
    }

    override suspend fun <T> fetchProducts(): Response<T> {
        val response = client.query(ShopifyProductsQuery()).execute()
        return when {
            response.hasErrors() -> Response.Error(response.errors?.first()?.message ?: "Data Not Found")
            else -> response.data?.products?.toProductList().let {
                return Response.Success(it as T)
            }
        }
    }


    override suspend fun <T> fetchBrands(): Response<T> {
        val response = client.query(ShopifyBrandsQuery()).execute()
        return when {
            response.hasErrors() -> Response.Error(response.errors?.first()?.message ?: "Data Not Found")
            else -> response.data?.collections?.toBrandsList().let {
                return Response.Success(it as T)
            }
        }
    }

    override suspend fun <T> fetchProductsByBrandId(id: String): Response<T> {
        val response = client.query(ShopifyBrandsByIdQuery(id)).execute()
        return when {
            response.hasErrors() -> Response.Error(response.errors?.first()?.message ?: "Data Not Found")
            else -> response.data?.collection?.products?.toProductList().let {
                it?.get(0)?.vendorImageUrl = (response.data?.collection?.image?.url ?: "").toString()
                return Response.Success(it as T)
            }
        }
    }
}
