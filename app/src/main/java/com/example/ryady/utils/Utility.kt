package com.example.ryady.utils

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.CustomerAccessTokenCreateMutation
import com.example.GetCustomerDataQuery
import kotlinx.coroutines.flow.collectLatest


private val Context.dataStore by preferencesDataStore(name = "settings")
private val userNameKey = stringPreferencesKey("userName")
private val userEmailKey = stringPreferencesKey("EmailKey")
private val userTokenKey = stringPreferencesKey("TokenKey")
private val userPhoneKey = stringPreferencesKey("PhoneKey")
private val countryCodeKey = stringPreferencesKey("CountryCode")
private val countryNameKey = stringPreferencesKey("CountryName")
private val currencyCodeKey = stringPreferencesKey("CurrencyCode")
private val currencyNameKey = stringPreferencesKey("CurrencyName")


suspend fun saveUserData(
    context: Context,
    customer: GetCustomerDataQuery.Customer,
    customerToken: String
) {
    context.dataStore.edit { settings ->
        settings[userEmailKey] = customer.email ?: "no email"
        settings[userNameKey] = "${customer.firstName} ${customer.lastName}"
        settings[userPhoneKey] = customer.phone ?: "null"
        settings[userTokenKey] = customerToken
    }
}

suspend fun readCustomerData(
    context: Context,
    customerData: (MutableMap<String, String>) -> Unit
) {
    val userData: MutableMap<String, String> = mutableMapOf()
    context.dataStore.data.collectLatest {
        userData["user name"] = it[userNameKey] ?: "no  name Value"
        userData["user email"] = it[userEmailKey] ?: "no email Value"
        userData["user token"] = it[userTokenKey] ?: "no token  Value"
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
    context: Context,
    countryData: (MutableMap<String, String>) -> Unit
) {
    val countryData: MutableMap<String, String> = mutableMapOf()
    context.dataStore.data.collectLatest {
        countryData["country name"] = it[countryNameKey] ?: "Egypt"
        countryData["country code"] = it[countryCodeKey] ?: "eg"
        countryData(countryData)
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
    context: Context,
    currencyData: (MutableMap<String, String>) -> Unit
) {
    val currencyData: MutableMap<String, String> = mutableMapOf()
    context.dataStore.data.collectLatest {
        currencyData["currency name"] = it[currencyNameKey] ?: "Egyptian Pound"
        currencyData["currency code"] = it[currencyCodeKey] ?: "EGP"
        currencyData(currencyData)
    }
}

// cart id
// check out url

