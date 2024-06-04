package com.example.ryady.cart.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ProductByIdQuery
import com.example.RetrieveCartQuery
import com.example.payment.PaymentRequest
import com.example.payment.State
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.network.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartViewModel(private val remoteDataSource: IRemoteDataSource) : ViewModel() {
    private var _cartInfo: MutableStateFlow<Response<RetrieveCartQuery.Cart>> = MutableStateFlow(
        Response.Loading())
    var cartInfo : StateFlow<Response<RetrieveCartQuery.Cart>> = _cartInfo
    private var _order= MutableStateFlow<State>(State.Loading)
    val order:StateFlow<State> = _order
    private var _updateCartItemInfo: MutableStateFlow<Response<Int>> = MutableStateFlow(Response.Loading())
    var updateCartItemInfo : StateFlow<Response<Int>>  = _updateCartItemInfo


    suspend fun updateCartLine(cartId: String,
                              lineID: String,
                              quantity: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val response = remoteDataSource.updateCartLine<Int>(cartId = cartId, lineID = lineID, quantity = quantity)
            when(response){
                is Response.Error -> _updateCartItemInfo.value = Response.Error(response.message)
                is Response.Loading -> _updateCartItemInfo.value = Response.Loading()
                is Response.Success -> _updateCartItemInfo.value = Response.Success(response.data)
            }
        }

    }

    suspend fun fetchCartById(id : String){
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.fetchCartById(id = id)
                .collectLatest {
                    _cartInfo.value = it
                }
        }

    }


    fun createPayment(paymentRequest: PaymentRequest){
        viewModelScope.launch(Dispatchers.IO) {
            remoteDataSource.createPayment(paymentRequest).collectLatest {
                if (it.isSuccessful) {
                    _order.value = State.Success(it.body()!!)
                }else{
                    Log.d("RES", it.errorBody()?.string() ?: "error")
                }
            }
        }
    }


}