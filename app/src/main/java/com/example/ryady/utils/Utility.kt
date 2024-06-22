package com.example.ryady.utils

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.GetCustomerDataQuery
import com.example.ryady.model.CustomerReview
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

val reviews = listOf(
    CustomerReview("John M.", 1.0, "The fabric quality is terrible. It shrank after the first wash.", "Male"),
    CustomerReview("Sarah T.", 1.2, "The color faded significantly after just one wash.", "Female"),
    CustomerReview("Mike P.", 1.5, "The stitching came apart after wearing it twice.", "Male"),
    CustomerReview("Emma W.", 1.7, "It's uncomfortable and itchy to wear.", "Female"),
    CustomerReview("Liam K.", 1.0, "Poor fit, not true to size at all.", "Male"),
    CustomerReview("Olivia R.", 1.3, "The material feels cheap and flimsy.", "Female"),
    CustomerReview("Noah L.", 1.6, "Buttons fell off after one use.", "Male"),
    CustomerReview("Ava B.", 1.8, "The color doesn't match the pictures online.", "Female"),
    CustomerReview("William C.", 2.0, "Too expensive for the quality provided.", "Male"),
    CustomerReview("Mia J.", 1.9, "The fabric pilled after a few wears.", "Female"),
    CustomerReview("James H.", 2.5, "It's okay but nothing special. Could be better.", "Male"),
    CustomerReview("Sophia G.", 2.8, "The fit is alright, but the material could be softer.", "Female"),
    CustomerReview("Benjamin N.", 3.0, "Decent quality but overpriced.", "Male"),
    CustomerReview("Isabella D.", 2.9, "Not bad, but I expected more based on the pictures.", "Female"),
    CustomerReview("Lucas V.", 3.5, "Fits well but the fabric is a bit thin.", "Male"),
    CustomerReview("Amelia S.", 3.2, "It's an average piece, good for casual wear.", "Female"),
    CustomerReview("Mason F.", 2.7, "The design is nice but the quality needs improvement.", "Male"),
    CustomerReview("Harper L.", 3.0, "An average purchase. Not too bad, not great either.", "Female"),
    CustomerReview("Ethan B.", 2.5, "Okay product. There are better options available.", "Male"),
    CustomerReview("Evelyn M.", 3.1, "Comfortable but fades quickly.", "Female"),
    CustomerReview("Alexander K.", 3.3, "Good fit but wrinkles easily.", "Male"),
    CustomerReview("Abigail Q.", 3.0, "The stitching is not the best. Could be improved.", "Female"),
    CustomerReview("Daniel A.", 2.9, "Average quality, expected more for the price.", "Male"),
    CustomerReview("Emily R.", 3.4, "Comfortable but doesn't hold up well over time.", "Female"),
    CustomerReview("Matthew T.", 3.2, "Nice design but the material is subpar.", "Male"),
    CustomerReview("Chloe W.", 3.6, "Fits nicely but not very durable.", "Female"),
    CustomerReview("Aiden Z.", 3.1, "It's fine, but wouldn't buy again.", "Male"),
    CustomerReview("Madison I.", 2.6, "Looks good but feels cheap.", "Female"),
    CustomerReview("Elijah S.", 3.0, "Just okay. Nothing to write home about.", "Male"),
    CustomerReview("Ella J.", 3.5, "Stylish but not the best quality.", "Female"),
    CustomerReview("Henry P.", 3.7, "Good for casual wear, but not very durable.", "Male"),
    CustomerReview("Scarlett N.", 2.8, "Material could be better. Average overall.", "Female"),
    CustomerReview("Jackson G.", 3.2, "Nice but could be improved.", "Male"),
    CustomerReview("Aria C.", 3.1, "Comfortable but quality is lacking.", "Female"),
    CustomerReview("Sebastian F.", 3.4, "Fits well but fades after a few washes.", "Male"),
    CustomerReview("Grace K.", 3.0, "Decent product, but there are better options.", "Female"),
    CustomerReview("Jack O.", 2.9, "Not the best quality, but not the worst either.", "Male"),
    CustomerReview("Lily M.", 3.5, "Nice design but material feels cheap.", "Female"),
    CustomerReview("Samuel T.", 3.0, "Okay product for the price, but not great.", "Male"),
    CustomerReview("Zoe R.", 3.2, "Comfortable but color fades quickly.", "Female"),
    CustomerReview("Michael V.", 4.0, "Good quality for the price. I'm satisfied.", "Male"),
    CustomerReview("Charlotte W.", 4.2, "Very comfortable and fits perfectly.", "Female"),
    CustomerReview("Joseph A.", 4.5, "Excellent product. Highly recommend!", "Male"),
    CustomerReview("Emily H.", 4.3, "Great fit and good quality material.", "Female"),
    CustomerReview("David B.", 4.1, "Nice design and comfortable to wear.", "Male"),
    CustomerReview("Mia P.", 4.7, "Love it! Will definitely buy again.", "Female"),
    CustomerReview("Chris L.", 4.8, "High quality and looks great.", "Male"),
    CustomerReview("Sophia G.", 4.6, "Very pleased with this purchase.", "Female"),
    CustomerReview("Andrew T.", 4.4, "Comfortable and stylish.", "Male"),
    CustomerReview("Olivia F.", 4.9, "Fantastic product! Exceeded my expectations.", "Female"),
    CustomerReview("Robert K.", 4.0, "Good value for money. Would recommend.", "Male"),
    CustomerReview("Ava S.", 4.3, "Fits well and feels nice.", "Female"),
    CustomerReview("Mark R.", 4.1, "Well-made and comfortable.", "Male"),
    CustomerReview("Isabella V.", 4.7, "Absolutely love it. Great quality.", "Female"),
    CustomerReview("Kevin M.", 4.2, "Very satisfied with this product.", "Male"),
    CustomerReview("Lily C.", 4.6, "Super comfortable and stylish.", "Female"),
    CustomerReview("Daniel W.", 4.4, "Good quality and fits perfectly.", "Male"),
    CustomerReview("Emma N.", 4.5, "Great product! Highly recommend.", "Female"),
    CustomerReview("Ryan Z.", 4.3, "Very comfortable and looks good.", "Male"),
    CustomerReview("Abigail E.", 4.9, "Amazing quality. Will purchase again.", "Female")
)

