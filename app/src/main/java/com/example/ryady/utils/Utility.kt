package com.example.ryady.utils

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.GetCustomerDataQuery
import kotlinx.coroutines.flow.collectLatest


private val Context.dataStore by preferencesDataStore(name = "settings")
private val userNameKey = stringPreferencesKey("userName")
private val firstNameKey = stringPreferencesKey("firstName")
private val lastNameKey = stringPreferencesKey("lastName")
private val userEmailKey = stringPreferencesKey("EmailKey")
private val userTokenKey = stringPreferencesKey("TokenKey")
private val userPhoneKey = stringPreferencesKey("PhoneKey")
private val countryCodeKey = stringPreferencesKey("CountryCode")
private val countryNameKey = stringPreferencesKey("CountryName")
private val currencyCodeKey = stringPreferencesKey("CurrencyCode")
private val currencyNameKey = stringPreferencesKey("CurrencyName")
private val cartIdKey = stringPreferencesKey("CartId")
private val checkoutUrlKey = stringPreferencesKey("CheckoutUrl")


suspend fun saveUserData(
    context: Context, customer: GetCustomerDataQuery.Customer, customerToken: String
) {
    context.dataStore.edit { settings ->
        settings[userEmailKey] = customer.email ?: "no email"
        settings[userNameKey] = "${customer.firstName} ${customer.lastName}"
        settings[firstNameKey] = customer.firstName ?: "no first name"
        settings[lastNameKey] = customer.lastName ?: "no last name"
        settings[userPhoneKey] = customer.phone ?: "null"
        settings[userTokenKey] = customerToken
        Log.i("TAG", "saveUserData: Saved User Data")
    }
}

suspend fun readCustomerData(
    context: Context, customerData: (MutableMap<String, String>) -> Unit
) {
    val userData: MutableMap<String, String> = mutableMapOf()
    context.dataStore.data.collectLatest {
        userData["user name"] = it[userNameKey] ?: "no  name Value"
        userData["first name"] = it[firstNameKey] ?: "no  name Value"
        userData["last name"] = it[lastNameKey] ?: "no  name Value"
        userData["user email"] = it[userEmailKey] ?: "no email Value"
        userData["user token"] = it[userTokenKey] ?: ""
        userData["user phone"] = it[userPhoneKey] ?: "no phone Value"
        customerData(userData)
    }
}

suspend fun saveCountry(
    context: Context, countryCode: String, countryName: String
) {
    context.dataStore.edit { settings ->
        settings[countryCodeKey] = countryCode
        settings[countryNameKey] = countryName
    }

}

suspend fun readCountry(
    context: Context, countryData: (MutableMap<String, String>) -> Unit
) {
    val countryDataMap: MutableMap<String, String> = mutableMapOf()
    context.dataStore.data.collectLatest {
        countryDataMap["country name"] = it[countryNameKey] ?: "Egypt"
        countryDataMap["country code"] = it[countryCodeKey] ?: "eg"
        countryData(countryDataMap)
    }
}

suspend fun saveCurrency(
    context: Context, currencyCode: String, currencyName: String
) {
    context.dataStore.edit { settings ->
        settings[currencyCodeKey] = currencyCode
        settings[currencyNameKey] = currencyName
    }
}

suspend fun readCurrency(
    context: Context, currencyData: (MutableMap<String, String>) -> Unit
) {
    val currencyDataMap: MutableMap<String, String> = mutableMapOf()
    context.dataStore.data.collectLatest {
        currencyDataMap["currency name"] = it[currencyNameKey] ?: "Egyptian Pound"
        currencyDataMap["currency code"] = it[currencyCodeKey] ?: "EGP"
        currencyData(currencyDataMap)
    }
}

suspend fun saveCart(context: Context, cardId: String, checkoutUrl: String) {
    context.dataStore.edit { settings ->
        settings[cartIdKey] = cardId
        settings[checkoutUrlKey] = checkoutUrl
    }
}

suspend fun readCart(
    context: Context, cartData: (MutableMap<String, String>) -> Unit
) {
    val cartDataMap: MutableMap<String, String> = mutableMapOf()
    context.dataStore.data.collectLatest {
        cartDataMap["cart id"] = it[cartIdKey] ?: ""
        cartDataMap["checkout url"] = it[checkoutUrlKey] ?: ""
        cartData(cartDataMap)
    }
}
