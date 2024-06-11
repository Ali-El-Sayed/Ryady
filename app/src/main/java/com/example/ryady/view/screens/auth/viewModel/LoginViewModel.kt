package com.example.ryady.view.screens.auth.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.CreateCartEmptyMutation
import com.example.CustomerCreateMutation
import com.example.GetCustomerDataQuery
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.network.model.Response
import com.example.type.CustomerAccessTokenCreateInput
import com.example.type.CustomerCreateInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val TAG = "LoginViewModel"

class LoginViewModel(private val remoteDataSource: IRemoteDataSource) : ViewModel() {

    private var _createdAccount: MutableStateFlow<Response<CustomerCreateMutation.Customer>> =
        MutableStateFlow(Response.Loading())
    val createdAccount: StateFlow<Response<CustomerCreateMutation.Customer>> = _createdAccount

    private var _loginAccountState: MutableStateFlow<Response<String>> =
        MutableStateFlow(Response.Loading())
    val loginAccountState: StateFlow<Response<String>> = _loginAccountState

    private var _customerData : MutableStateFlow<Response<GetCustomerDataQuery.Customer>> =MutableStateFlow(Response.Loading())
    val customerData : StateFlow<Response<GetCustomerDataQuery.Customer>> = _customerData

    private var _createCartState: MutableStateFlow<Response<Pair<String, String>>> =
        MutableStateFlow(Response.Loading())
    val createCartState: StateFlow<Response<Pair<String, String>>> = _createCartState

    fun createAccount(newCustomerAccount: CustomerCreateInput) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = remoteDataSource.createCustomer<CustomerCreateMutation.Customer>(newCustomerAccount)
            _createdAccount.value = data
        }
    }

    fun createAccountFirebase(userAccount: CustomerCreateInput) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.createAccountUsingFirebase(userAccount)
        }
    }

    fun checkVerification(
        userAccount: CustomerCreateInput,
        isVerified: (verified: Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.checkVerification(userAccount, isVerified)
        }
    }

    fun loginToAccount(userAccount: CustomerAccessTokenCreateInput) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.createAccessToken<String>(userAccount).collect {
                _loginAccountState.value = it
            }
        }
    }

    fun getUserCustomerData(customerToken : String){
        viewModelScope.launch {
            remoteDataSource.getCustomerData<GetCustomerDataQuery.Customer>(customerToken).collectLatest {
               _customerData.value = it
            }
        }
    }

    fun createEmptyCart(email:String , token : String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = remoteDataSource.createEmptyCart<Pair<String, String>>(email = email, token = token)
            when (response) {
                is Response.Error -> {}
                is Response.Loading -> {}
                is Response.Success -> {
                    _createCartState.value = Response.Success(response.data)
                }
            }

        }
    }
}