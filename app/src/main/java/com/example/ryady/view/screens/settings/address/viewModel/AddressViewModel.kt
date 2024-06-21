package com.example.ryady.view.screens.settings.address.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Address
import com.example.ryady.network.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AddressViewModel"

class AddressViewModel(private val remote: IRemoteDataSource, var userToken: String = "") : ViewModel() {
    private val _addresses: MutableStateFlow<Response<ArrayList<Address>>> = MutableStateFlow(Response.Loading())
    val addresses = _addresses.asStateFlow()
    var address: Address = Address()

    // for saving address state
    private val _isAddressSaved: MutableStateFlow<Response<Boolean>> = MutableStateFlow(Response.Error(""))
    val isAddressSaved = _isAddressSaved.asStateFlow()

    suspend fun fetchAddresses() {
        viewModelScope.launch(Dispatchers.IO) {
            if (userToken.isNotEmpty()) _addresses.emit(remote.fetchAddresses(userToken))
            else _addresses.emit(Response.Error("No token provided"))
        }
    }

    suspend fun deleteAddress(addressId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            remote.deleteAddress(userToken, addressId)
//            fetchAddresses()
        }
    }

    suspend fun saveAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            _isAddressSaved.emit(Response.Loading())
            if (userToken.isNotEmpty()) {
                val response = async { remote.createUserAddress<Boolean>(userToken, address) }.await()
                when (response) {
                    is Response.Loading -> _isAddressSaved.emit(Response.Loading())
                    is Response.Error -> _isAddressSaved.emit(response)
                    is Response.Success -> _isAddressSaved.emit(response)
                }
            }
        }
    }
}