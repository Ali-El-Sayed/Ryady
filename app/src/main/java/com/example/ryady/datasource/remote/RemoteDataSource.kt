package com.example.ryady.datasource.remote

import com.apollographql.apollo3.ApolloClient
import com.example.ProductByIdQuery
import com.example.ShopifyProductsQuery
import com.example.ryady.model.extensions.toProductList
import com.example.ryady.network.model.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TAG = "RemoteDataSource"

interface IRemoteDataSource {

    suspend fun <T> fetchProducts(): Response<T>

    suspend fun  fetchProductById(id:String): Flow<Response<ProductByIdQuery.Product>>


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
        client.query(ShopifyProductsQuery()).execute().data?.products?.toProductList()
            ?.let {
                return Response.Success(it as T)
            }
        return Response.Error("Data Not Found")
    }

    override suspend fun fetchProductById(id:String): Flow<Response<ProductByIdQuery.Product>> {
       client.query(ProductByIdQuery(id))
            .execute().data?.product?.let {
                return flow { emit(Response.Success(it)) }
            }

        return flow { emit(Response.Error("Error get data from remote")) }
    }
}
