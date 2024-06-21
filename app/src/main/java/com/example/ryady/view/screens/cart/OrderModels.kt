package com.example.ryady.view.screens.cart

import com.google.gson.annotations.SerializedName

data class OrderRequest(

    @SerializedName("order") var order: Order? = Order()

)

data class LineItems(

    @SerializedName("variant_id") var variantId: Int? = null,
    @SerializedName("quantity") var quantity: Int? = null

)

data class BillingAddress(

    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("province") var province: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("zip") var zip: String? = null

)

data class ShippingAddress(

    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("province") var province: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("zip") var zip: String? = null

)

data class DiscountCodes(

    @SerializedName("code") var code: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("type") var type: String? = null

)

data class Order(

    @SerializedName("line_items") var lineItems: ArrayList<LineItems> = arrayListOf(),
    @SerializedName("send_receipt") var sendReceipt: String? = "true",
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("billing_address") var billingAddress: ShippingAddress? = ShippingAddress(),
    @SerializedName("shipping_address") var shippingAddress: ShippingAddress? = ShippingAddress(),
    @SerializedName("financial_status") var financialStatus: String? = null,
    @SerializedName("discount_codes") var discountCodes: ArrayList<DiscountCodes> = arrayListOf()

)