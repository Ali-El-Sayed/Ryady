package com.example.ryady.view.screens.cart.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.CreateCartEmptyMutation
import com.example.RetrieveCartQuery
import com.example.payment.State
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Order
import com.example.ryady.network.model.Response
import com.example.ryady.utils.saveCart
import com.example.type.CartLineInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "CartViewModel"

class CartViewModel(private val remoteDataSource: IRemoteDataSource) : ViewModel() {
    var email = ""
    private var _cartInfo: MutableStateFlow<Response<RetrieveCartQuery.Cart>> = MutableStateFlow(
        Response.Loading()
    )
    var cartInfo: StateFlow<Response<RetrieveCartQuery.Cart>> = _cartInfo
    private var _order = MutableStateFlow<State>(State.Loading)
    val order: StateFlow<State> = _order
    private var _updateCartItemInfo: MutableStateFlow<Response<Int>> = MutableStateFlow(Response.Loading())
    var updateCartItemInfo: StateFlow<Response<Int>> = _updateCartItemInfo
    private var _cartCreate: MutableStateFlow<Response<Pair<String, String>>> = MutableStateFlow(Response.Loading())
    var cartCreate: StateFlow<Response<Pair<String, String>>> = _cartCreate
    private var _createCartState: MutableStateFlow<Response<Pair<String, String>>> =
        MutableStateFlow(Response.Loading())
    val createCartState: StateFlow<Response<Pair<String, String>>> = _createCartState

    var currentOrder: Order = Order()
    var checkoutUrl = ""
    var cartId =""
     var userToken = ""
    suspend fun createOrderInformation() {
        viewModelScope.launch {
            remoteDataSource.createOrderInformation(
                userToken, currentOrder
            )
        }
    }

    suspend fun updateCartLine(
        cartId: String, lineID: String, quantity: Int
    ) {
        _updateCartItemInfo.value = Response.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            val response = remoteDataSource.updateCartLine<Int>(cartId = cartId, lineID = lineID, quantity = quantity)
            when (response) {
                is Response.Error -> _updateCartItemInfo.value = Response.Error(response.message)
                is Response.Loading -> _updateCartItemInfo.value = Response.Loading()
                is Response.Success -> _updateCartItemInfo.value = Response.Success(response.data)
            }
        }

    }

    suspend fun deleteCartLine(
        cartId: String, lineID: String
    ) {
        _updateCartItemInfo.value = Response.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            val response = remoteDataSource.deleteCartLine<Int>(cartId = cartId, lineID = lineID)
            when (response) {
                is Response.Error -> _updateCartItemInfo.value = Response.Error(response.message)
                is Response.Loading -> _updateCartItemInfo.value = Response.Loading()
                is Response.Success -> _updateCartItemInfo.value = Response.Success(response.data)
            }
        }
    }

    suspend fun fetchCartById() {
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.fetchCartById(id = cartId).collectLatest {
                when (it) {
                    is Response.Error -> {}
                    is Response.Loading -> _cartInfo.value = Response.Loading()
                    is Response.Success -> {
                        it.data.buyerIdentity.customer?.firstName
                        checkoutUrl = it.data.checkoutUrl.toString()
                        Log.d(TAG, "fetchCartById: ${checkoutUrl}")
                        _cartInfo.value = it
                    }
                }
            }
        }
    }

    suspend fun createCartWithLines(lines: List<CartLineInput>, customerToken: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = remoteDataSource.createCartWithLines<Pair<String, String>>(lines, customerToken, email)
            when (response) {
                is Response.Error -> {}
                is Response.Loading -> {}
                is Response.Success -> {
                    cartId = response.data.first
                    checkoutUrl = response.data.second

                    Log.d(TAG, "createCartWithLines: $cartId")
                    Log.d(TAG, "createCartWithLines: $checkoutUrl")
                    _cartCreate.value = Response.Success(response.data)
                }
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
                    cartId = response.data.first
                    checkoutUrl = response.data.second

                    Log.d(TAG, "createCartWithLines: $cartId")
                    Log.d(TAG, "createCartWithLines: $checkoutUrl")
                    _createCartState.value = Response.Success(response.data)
                }
            }

        }
    }

}