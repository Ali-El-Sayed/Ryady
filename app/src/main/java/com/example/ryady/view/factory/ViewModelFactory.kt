package com.example.ryady.view.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.view.screens.home.viewmodel.HomeViewModel

class ViewModelFactory(private val remote: IRemoteDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(remote) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}