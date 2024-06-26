package com.example.ryady.view.screens.cart.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.RetrieveCartQuery
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.datasource.remote.util.RemoteDSUtils
import com.example.ryady.model.Address
import com.example.ryady.model.CustomerCartData
import com.example.ryady.network.model.Response
import com.example.ryady.utils.saveCart
import com.example.ryady.view.screens.cart.OrderRequest
import com.example.type.CartLineInput
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "CartViewModel"

class CartViewModel(
    private val remoteDataSource: IRemoteDataSource, private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    var email = ""
    private var _cartInfo: MutableStateFlow<Response<RetrieveCartQuery.Cart>> = MutableStateFlow(
        Response.Loading()
    )
    var cartInfo: StateFlow<Response<RetrieveCartQuery.Cart>> = _cartInfo
    private var _updateCartItemInfo: MutableStateFlow<Response<Int>> = MutableStateFlow(Response.Loading())
    var updateCartItemInfo: StateFlow<Response<Int>> = _updateCartItemInfo
    private var _cartCreate: MutableStateFlow<Response<Pair<String, String>>> = MutableStateFlow(Response.Loading())
    var cartCreate: StateFlow<Response<Pair<String, String>>> = _cartCreate
    private var _createCartState: MutableStateFlow<Response<Pair<String, String>>> = MutableStateFlow(Response.Loading())
    private var _orderCreateInfo: MutableStateFlow<Response<Int>> = MutableStateFlow(Response.Loading())
    var orderCreateInfo: StateFlow<Response<Int>> = _orderCreateInfo
    private val _addresses: MutableStateFlow<Response<ArrayList<Address>>> = MutableStateFlow(Response.Loading())
    val addresses = _addresses.asStateFlow()
    var checkoutUrl = ""
    var cartId = ""
    var userToken = ""


    suspend fun updateCartLine(
        cartId: String, lineID: String, quantity: Int
    ) {
        _updateCartItemInfo.value = Response.Loading()
        viewModelScope.launch(dispatcher) {
            val response = remoteDataSource.updateCartLine<Int>(
                cartId = cartId, lineID = lineID, quantity = quantity
            )
            when (response) {
                is Response.Error -> _updateCartItemInfo.value = Response.Error(response.message)
                is Response.Loading -> _updateCartItemInfo.value = Response.Loading()
                is Response.Success -> _updateCartItemInfo.value = Response.Success(response.data)
            }
        }

    }

    suspend fun deleteCartLine(
        cartId: String, lineID: ArrayList<String>
    ) {
        _updateCartItemInfo.value = Response.Loading()
        viewModelScope.launch(dispatcher) {
            val response = remoteDataSource.deleteCartLine<Int>(cartId = cartId, lineID = lineID)
            when (response) {
                is Response.Error -> _updateCartItemInfo.value = Response.Error(response.message)
                is Response.Loading -> _updateCartItemInfo.value = Response.Loading()
                is Response.Success -> _updateCartItemInfo.value = Response.Success(response.data)
            }
        }
    }

    suspend fun fetchCartById() {
        viewModelScope.launch(dispatcher) {
            remoteDataSource.fetchCartById(id = cartId).collectLatest {
                when (it) {
                    is Response.Error -> {}
                    is Response.Loading -> _cartInfo.value = Response.Loading()
                    is Response.Success -> {
                        checkoutUrl = it.data.checkoutUrl.toString()
                        _cartInfo.value = it
                    }
                }
            }
        }
    }

    suspend fun createCartWithLines(
        lines: List<CartLineInput>, customerToken: String, email: String
    ) {
        viewModelScope.launch(dispatcher) {
            val response = remoteDataSource.createCartWithLines<Pair<String, String>>(
                lines, customerToken, email
            )
            when (response) {
                is Response.Error -> {}
                is Response.Loading -> {}
                is Response.Success -> {
                    cartId = response.data.first
                    checkoutUrl = response.data.second
                    _cartCreate.value = Response.Success(response.data)
                }
            }

        }

    }

    fun createEmptyCart(email: String, token: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = remoteDataSource.createEmptyCart<Pair<String, String>>(email = email, token = token)
            when (response) {
                is Response.Error -> {}

                is Response.Loading -> {}
                is Response.Success -> {
                    cartId = response.data.first
                    checkoutUrl = response.data.second
                    saveCart(context)
                    _createCartState.value = Response.Success(response.data)
                }
            }

        }
    }

    private suspend fun saveCart(context: Context) {
        saveCart(context, cartId, checkoutUrl)
        // save to firebase
        val database = FirebaseDatabase.getInstance("https://ryady-bf500-default-rtdb.europe-west1.firebasedatabase.app/")
        val customerRef = database.getReference("CustomerCart")
        val customerCartData = CustomerCartData(cartId, checkoutUrl)

        // Encode the email
        val encodedEmail = RemoteDSUtils.encodeEmail(email)

        // Save the data to the database
        customerRef.child(encodedEmail).setValue(customerCartData).addOnSuccessListener {}.addOnFailureListener {}
    }

    suspend fun fetchAddresses() {
        viewModelScope.launch(dispatcher) {
            if (userToken.isNotEmpty()) _addresses.emit(remoteDataSource.fetchAddresses(userToken))
            else _addresses.emit(Response.Error("No token provided"))
        }
    }

    suspend fun createOrder(orderRequest: OrderRequest) {
        viewModelScope.launch(dispatcher) {
            val response = remoteDataSource.createOrder(orderRequest)
            _orderCreateInfo.emit(Response.Loading())
            when (response) {
                0 -> _orderCreateInfo.emit(Response.Error("error"))
                1 -> _orderCreateInfo.emit(Response.Success(response))
            }

        }
    }

}