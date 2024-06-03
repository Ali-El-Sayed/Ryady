package com.example.ryady.model

import com.example.ryady.Variant
import com.example.type.Merchandise
import com.google.gson.annotations.SerializedName


data class Cart (

    @SerializedName("checkoutUrl"   ) var checkoutUrl   : String?           = null,
    @SerializedName("createdAt"     ) var createdAt     : String?           = null,
    @SerializedName("id"            ) var id            : String?           = null,
    @SerializedName("note"          ) var note          : String?           = null,
    @SerializedName("totalQuantity" ) var totalQuantity : Int?              = null,
    @SerializedName("updatedAt"     ) var updatedAt     : String?           = null,
    @SerializedName("cost"          ) var cost          : Cost?             = Cost(),
    @SerializedName("estimatedCost" ) var estimatedCost : EstimatedCost?    = EstimatedCost(),
    @SerializedName("buyerIdentity" ) var buyerIdentity : BuyerIdentity?    = BuyerIdentity(),
    @SerializedName("lines"         ) var lines         : Lines?            = Lines(),
    @SerializedName("discountCodes" ) var discountCodes : ArrayList<String> = arrayListOf()

)

data class BuyerIdentity (

    @SerializedName("countryCode"                ) var countryCode                : String?           = null,
    @SerializedName("email"                      ) var email                      : String?           = null,
    @SerializedName("phone"                      ) var phone                      : String?           = null,
    @SerializedName("walletPreferences"          ) var walletPreferences          : ArrayList<String> = arrayListOf(),
    @SerializedName("deliveryAddressPreferences" ) var deliveryAddressPreferences : ArrayList<String> = arrayListOf(),
    @SerializedName("customer"                   ) var customer                   : String?           = null

)
data class Lines (

    @SerializedName("edges" ) var edges : ArrayList<CartEdges> = arrayListOf()

)
data class CartEdges (

    @SerializedName("cursor" ) var cursor : String? = null,
    @SerializedName("node"   ) var node   : Node?   = Node()

)
data class Node (

    @SerializedName("id"                  ) var id                  : String?           = null,
    @SerializedName("merchandise"         ) var merchandise         : CartMerchandise?      = CartMerchandise(),
    @SerializedName("quantity"            ) var quantity            : Int?              = null,
    @SerializedName("cost"                ) var cost                : Cost?             = Cost(),
    @SerializedName("discountAllocations" ) var discountAllocations : ArrayList<String> = arrayListOf()

)
data class CartMerchandise (

    @SerializedName("availableForSale"    ) var availableForSale    : Boolean? = null,
    @SerializedName("barcode"             ) var barcode             : String?  = null,
    @SerializedName("currentlyNotInStock" ) var currentlyNotInStock : Boolean? = null,
    @SerializedName("id"                  ) var id                  : String?  = null,
    @SerializedName("quantityAvailable"   ) var quantityAvailable   : Int?     = null,
    @SerializedName("requiresShipping"    ) var requiresShipping    : Boolean? = null,
    @SerializedName("sku"                 ) var sku                 : String?  = null,
    @SerializedName("taxable"             ) var taxable             : Boolean? = null,
    @SerializedName("title"               ) var title               : String?  = null,
    @SerializedName("weight"              ) var weight              : Int?     = null,
    @SerializedName("weightUnit"          ) var weightUnit          : String?  = null,
    @SerializedName("image"               ) var image               : Image?   = Image(),
    @SerializedName("price"               ) var price               : Price?   = Price(),
    @SerializedName("unitPrice"           ) var unitPrice           : String?  = null

)

data class Price (

    @SerializedName("amount"       ) var amount       : String? = null,
    @SerializedName("currencyCode" ) var currencyCode : String? = null

)
data class Image (

    @SerializedName("altText"        ) var altText        : String? = null,
    @SerializedName("height"         ) var height         : Int?    = null,
    @SerializedName("id"             ) var id             : String? = null,
    @SerializedName("originalSrc"    ) var originalSrc    : String? = null,
    @SerializedName("src"            ) var src            : String? = null,
    @SerializedName("transformedSrc" ) var transformedSrc : String? = null,
    @SerializedName("url"            ) var url            : String? = null,
    @SerializedName("width"          ) var width          : Int?    = null

)
data class TotalAmount (

    @SerializedName("amount"       ) var amount       : String? = null,
    @SerializedName("currencyCode" ) var currencyCode : String? = null

)
data class CheckoutChargeAmount (

    @SerializedName("amount"       ) var amount       : String? = null,
    @SerializedName("currencyCode" ) var currencyCode : String? = null

)
data class Cost (

    @SerializedName("subtotalAmountEstimated" ) var subtotalAmountEstimated : Boolean?              = null,
    @SerializedName("totalAmountEstimated"    ) var totalAmountEstimated    : Boolean?              = null,
    @SerializedName("totalAmount"             ) var totalAmount             : TotalAmount?          = TotalAmount(),
    @SerializedName("checkoutChargeAmount"    ) var checkoutChargeAmount    : CheckoutChargeAmount? = CheckoutChargeAmount()

)
data class SubtotalAmount (

    @SerializedName("amount"       ) var amount       : String? = null,
    @SerializedName("currencyCode" ) var currencyCode : String? = null

)
data class EstimatedCost (

    @SerializedName("subtotalAmount" ) var subtotalAmount : SubtotalAmount? = SubtotalAmount(),
    @SerializedName("totalAmount"    ) var totalAmount    : TotalAmount?    = TotalAmount()

)