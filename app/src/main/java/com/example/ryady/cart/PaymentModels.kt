package com.example.payment

import com.google.gson.annotations.SerializedName

data class Item(
    val name: String,
    val amount: Int,
    val description: String,
    val quantity: Int
)
data class BillingData(
    val apartment: String="dumy",
    val first_name: String,
    val last_name: String,
    val street: String,
    val building: String,
    val phone_number: String,
    val city: String,
    val country: String,
    val email: String,
    val floor: String="dumy",
    val state: String="dumy"
)
data class PaymentRequest(
    val amount: Int,
    val currency: String,
    val payment_methods: List<Int>,
    val items: List<Item>,
    val billing_data: BillingData
)
data class PaymentCreationResult (

    @SerializedName("payment_keys"      ) var paymentKeys      : ArrayList<PaymentKeys>    = arrayListOf(),
    @SerializedName("id"                ) var id               : String?                   = null,
    @SerializedName("intention_detail"  ) var intentionDetail  : IntentionDetail?          = IntentionDetail(),
    @SerializedName("client_secret"     ) var clientSecret     : String?                   = null,
    @SerializedName("payment_methods"   ) var paymentMethods   : ArrayList<PaymentMethods> = arrayListOf(),
    @SerializedName("special_reference" ) var specialReference : String?                   = null,
    @SerializedName("extras"            ) var extras           : Extras?                   = Extras(),
    @SerializedName("confirmed"         ) var confirmed        : Boolean?                  = null,
    @SerializedName("status"            ) var status           : String?                   = null,
    @SerializedName("created"           ) var created          : String?                   = null,
    @SerializedName("card_detail"       ) var cardDetail       : String?                   = null,

)
data class PaymentKeys (

    @SerializedName("integration"  ) var integration : Int?    = null,
    @SerializedName("key"          ) var key         : String? = null,
    @SerializedName("gateway_type" ) var gatewayType : String? = null,
    @SerializedName("iframe_id"    ) var iframeId    : String? = null

)
data class Items (

    @SerializedName("name"        ) var name        : String? = null,
    @SerializedName("amount"      ) var amount      : Int?    = null,
    @SerializedName("description" ) var description : String? = null,
    @SerializedName("quantity"    ) var quantity    : Int?    = null

)
data class IntentionDetail (

    @SerializedName("amount"   ) var amount   : Int?             = null,
    @SerializedName("items"    ) var items    : ArrayList<Items> = arrayListOf(),
    @SerializedName("currency" ) var currency : String?          = null

)
data class PaymentMethods (

    @SerializedName("integration_id"    ) var integrationId  : Int?     = null,
    @SerializedName("alias"             ) var alias          : String?  = null,
    @SerializedName("name"              ) var name           : String?  = null,
    @SerializedName("method_type"       ) var methodType     : String?  = null,
    @SerializedName("currency"          ) var currency       : String?  = null,
    @SerializedName("live"              ) var live           : Boolean? = null,
    @SerializedName("use_cvc_with_moto" ) var useCvcWithMoto : Boolean? = null

)
data class Extras (

    @SerializedName("creation_extras"     ) var creationExtras     : String? = null,
    @SerializedName("confirmation_extras" ) var confirmationExtras : String? = null

)