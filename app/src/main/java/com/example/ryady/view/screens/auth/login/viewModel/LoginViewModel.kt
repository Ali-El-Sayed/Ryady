package com.example.ryady.view.screens.auth.login.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.CustomerCreateMutation
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.network.model.Response
import com.example.type.CustomerAccessTokenCreateInput
import com.example.type.CustomerCreateInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


private const val TAG = "LoginViewModel"

class LoginViewModel(private val remoteDataSource: IRemoteDataSource) : ViewModel() {

    private var _createdAccount: MutableStateFlow<Response<CustomerCreateMutation.Customer>> =
        MutableStateFlow(Response.Loading())

    val createdAccount:StateFlow<Response<CustomerCreateMutation.Customer>> = _createdAccount

    private  var _loginAccountState : MutableStateFlow<Response<String>> = MutableStateFlow(Response.Loading())

    val loginAccountState : StateFlow<Response<String>> = _loginAccountState
    suspend fun  createAccount(newCustomerAccount: CustomerCreateInput) {
        Log.i(TAG, "createAccount: ")
        viewModelScope.launch(Dispatchers.IO) {
            val data = remoteDataSource.createCustomer<CustomerCreateMutation.Customer>(newCustomerAccount)
            _createdAccount.value = data
            Log.i(TAG, "createAccount: $data")
        }
    }

    suspend fun loginToAccount(userAccount : CustomerAccessTokenCreateInput){

        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.createAccessToken<String>(userAccount).collect {
                _loginAccountState.value = it
                Log.i(TAG, "loginToAccount: ${_loginAccountState.value}")
            }
        }
    }
}