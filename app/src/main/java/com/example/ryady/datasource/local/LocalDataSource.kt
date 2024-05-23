package com.example.ryady.datasource.local

interface ILocalDataSource {
    fun isUserLoggedIn(): Boolean
}

class LocalDataSource : ILocalDataSource {
    override fun isUserLoggedIn(): Boolean {
        TODO("Not yet implemented")
    }
}
