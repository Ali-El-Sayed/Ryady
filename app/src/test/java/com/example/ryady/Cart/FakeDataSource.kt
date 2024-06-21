package com.example.ryady.Cart

import com.example.ProductByIdQuery
import com.example.RetrieveCartQuery
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Address
import com.example.ryady.model.Currency
import com.example.ryady.model.Product
import com.example.ryady.model.Symbols
import com.example.type.CartLineInput
import com.example.type.CurrencyCode
import com.example.type.CustomerAccessTokenCreateInput
import com.example.type.CustomerCreateInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeDataSource : IRemoteDataSource {
    override suspend fun fetchCountries(): Flow<Response<HashMap<String, String>>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAllCurrencies(): Flow<Response<Symbols>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchExchangeRates(): Flow<Response<Currency>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchProducts(): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchProductById(id: String): Flow<com.example.ryady.network.model.Response<ProductByIdQuery.Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> createAccessToken(customer: CustomerAccessTokenCreateInput): Flow<com.example.ryady.network.model.Response<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCartById(id: String): Flow<com.example.ryady.network.model.Response<RetrieveCartQuery.Cart>> {
        var dummyCart = RetrieveCartQuery.Cart(
            checkoutUrl = "https://mad44-android-sv-1.myshopify.com/cart/c/Z2NwLWV1cm9wZS13ZXN0MTowMUowUkozNFMwOUNUSDYyUU1ZWUNONVRONg?key=8e77553670b039b9a8ac563c991820ac",
            createdAt = "2024-06-19T15:33:22Z",
            id = "gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUowUkozNFMwOUNUSDYyUU1ZWUNONVRONg?key=8e77553670b039b9a8ac563c991820ac",
            note = "",
            totalQuantity = 0,
            updatedAt = "2024-06-19T15:33:22Z",
            cost = RetrieveCartQuery.Cost(
                subtotalAmountEstimated = true,
                totalAmountEstimated = true,
                totalAmount = RetrieveCartQuery.TotalAmount(
                    amount = "0.0",
                    currencyCode = CurrencyCode.EGP
                ),
                checkoutChargeAmount = RetrieveCartQuery.CheckoutChargeAmount(
                    amount = "0.0",
                    currencyCode = CurrencyCode.EGP
                )
            ),
            estimatedCost = RetrieveCartQuery.EstimatedCost(
                subtotalAmount = RetrieveCartQuery.SubtotalAmount(
                    amount = "0.0",
                    currencyCode = CurrencyCode.EGP
                ),
                totalAmount = RetrieveCartQuery.TotalAmount1(
                    amount = "0.0",
                    currencyCode = CurrencyCode.EGP
                )
            ),
            buyerIdentity = RetrieveCartQuery.BuyerIdentity(
                countryCode = null,
                email = "ahmeditighoneim@gmail.com",
                phone = null,
                walletPreferences = listOf(),
                deliveryAddressPreferences = listOf(),
                customer = RetrieveCartQuery.Customer(
                    acceptsMarketing = false,
                    createdAt = "2024-06-14T16:01:31Z",
                    displayName = "ahmed huss",
                    email = "ahmeditighoneim@gmail.com",
                    firstName = "ahmed",
                    id = "gid://shopify/Customer/7448707039315",
                    lastName = "huss",
                    numberOfOrders = "0",
                    phone = null,
                    tags = listOf(),
                    updatedAt = "2024-06-19T15:33:22Z"
                )
            ),
            lines = RetrieveCartQuery.Lines(
                edges = listOf()
            ),
            discountCodes = listOf()
        )
        return flow { emit(com.example.ryady.network.model.Response.Success(dummyCart)) }

    }

    override suspend fun <T> fetchBrands(): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchProductsByBrandId(id: String): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> createCustomer(newCustomer: CustomerCreateInput): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> createCartWithLines(
        lines: List<CartLineInput>,
        customerToken: String,
        email: String
    ): com.example.ryady.network.model.Response<T> {
        return com.example.ryady.network.model.Response.Success(
            Pair(
                first = "idlines",
                second = "urllines"
            ) as T
        )
    }

    override suspend fun <T> addItemToCart(
        cartId: String,
        varientID: String,
        quantity: Int
    ): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> updateCartLine(
        cartId: String,
        lineID: String,
        quantity: Int
    ): com.example.ryady.network.model.Response<T> {
        return com.example.ryady.network.model.Response.Success(1 as T)
    }

    override suspend fun <T> deleteCartLine(
        cartId: String,
        lineID: String
    ): com.example.ryady.network.model.Response<T> {
        return com.example.ryady.network.model.Response.Success(1 as T)
    }

    override suspend fun <T> searchForProducts(itemName: String): Flow<com.example.ryady.network.model.Response<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchProductsByCategory(category: String): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun addItemToFavourite(email: String, product: ProductByIdQuery.Product) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFavouriteItem(
        email: String,
        productListL: (products: List<Product>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(email: String, itemId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun searchForAnItem(
        email: String,
        itemId: String,
        isFound: (found: Boolean) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun createAccountUsingFirebase(newCustomer: CustomerCreateInput) {
        TODO("Not yet implemented")
    }

    override suspend fun checkVerification(
        newCustomer: CustomerCreateInput,
        isVerified: (isVerified: Boolean) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun <T> getCustomerData(token: String): Flow<com.example.ryady.network.model.Response<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> createEmptyCart(
        email: String,
        token: String
    ): com.example.ryady.network.model.Response<T> {
        return com.example.ryady.network.model.Response.Success(
            Pair(
                first = "idempty",
                second = "urlempty"
            ) as T
        )
    }

    override suspend fun <T> fetchAddresses(token: String): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAddress(token: String, addressId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun <T> createUserAddress(
        token: String,
        address: Address
    ): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchOrders(userToken: String): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }
}