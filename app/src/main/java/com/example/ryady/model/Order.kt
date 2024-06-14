package com.example.ryady.model

data class Order(
    var orderNumber: String = "",
    var orderName: String = "",
    var address: Address = Address(),
    var items: List<Item> = emptyList(),
    var processedAt: String = "",
    var customerId: String = "",
    var customerFirstName: String = "",
    var customerLastName: String = "",
    var customerPhoneNumbers: String = "",
    var shippingAddress: String = "",
    var countryName: String = "",
    var postalCode: String = "",
    var city: String = "",
    var totalPrice: String = "",
    var currencyCode: String = "",
    var createdAt: String = "",
)

data class Item(
    var productId: String = "",
    var productName: String = "",
    var quantity: Int = 0,
    var thumbnailUrl: String = "",
    var price: String = "",
    var currencyCode: String = "",
    var vendor: String = "",
)

