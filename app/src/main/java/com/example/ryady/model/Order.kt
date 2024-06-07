package com.example.ryady.model

data class Order(
    var orderId: String = "",
    var items: List<Item> = emptyList(),
    var customerId: String = "",
    var customerFirstName: String = "",
    var customerLastName: String = "",
    var customerPhoneNumbers: String = "",
    var shippingAddress: String = "",
    var countryName: String = "",
    var countryCode: String = "",
    var postalCode: String = "",
    var city: String = "",
    var customerEmail: String = "",
    var totalPrice: Double = 0.0,
    var promoCode: String = "",
    var totalDiscount: Double = 0.0,
    var currency: String = "",
    var createdAt: String = "",
    var paymentMethod: PaymentMethod = PaymentMethod.CREDIT_CARD,
)

data class Item(
    var productId: String = "",
    var quantity: Int = 0,
    var thumbnailUrl: String = "",
    var price: Double = 0.0,
)

enum class PaymentMethod {
    CASH_ON_DELIVERY, CREDIT_CARD
}

