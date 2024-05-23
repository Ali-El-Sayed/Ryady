package com.example.ryady.repository

import com.example.ryady.datasource.local.ILocalDataSource
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Product
import com.example.ryady.network.model.Response

interface IRepository {
    suspend fun getProducts(): Response<ArrayList<Product>>
}

class Repository(
    private val remote: IRemoteDataSource,
    private val local: ILocalDataSource,
) : IRepository {
    override suspend fun getProducts(): Response<ArrayList<Product>> {
        return remote.fetchProducts()
    }
}
