package com.example.ryady.fakeRepo

import com.example.ProductByIdQuery
import com.example.RetrieveCartQuery
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Address
import com.example.ryady.model.Currency
import com.example.ryady.model.Order
import com.example.ryady.model.Product
import com.example.ryady.model.Symbols
import com.example.ryady.network.model.Response
import com.example.type.CartLineInput
import com.example.type.CurrencyCode
import com.example.type.CustomerAccessTokenCreateInput
import com.example.type.CustomerCreateInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRemoteDataSource : IRemoteDataSource {

    val favouriteList : MutableMap<String,MutableList<ProductByIdQuery.Product>> = mutableMapOf()
    override suspend fun fetchCountries(): Flow<retrofit2.Response<HashMap<String, String>>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAllCurrencies(): Flow<retrofit2.Response<Symbols>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchExchangeRates(): Flow<retrofit2.Response<Currency>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchProducts(): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchProductById(id: String): Flow<com.example.ryady.network.model.Response<ProductByIdQuery.Product>> {
        return flowOf(
            Response.Success(
                ProductByIdQuery.Product(
                    id = id,
                    title = "title$id",
                    description = "description$id",
                    images = ProductByIdQuery.Images(listOf()),
                    descriptionHtml = "descriptionHtml$id",
                    priceRange = ProductByIdQuery.PriceRange(
                        ProductByIdQuery.MaxVariantPrice(
                            id,
                            CurrencyCode.EGP
                        ), minVariantPrice = ProductByIdQuery.MinVariantPrice(amount = id, currencyCode = CurrencyCode.EGP)
                    ),

                    tags = listOf(),
                    totalInventory = id.toInt(),
                    vendor = "vendor$id",
                    variants = ProductByIdQuery.Variants(edges = listOf(), nodes = listOf())
                )
            )
        )
    }

    override suspend fun <T> createAccessToken(customer: CustomerAccessTokenCreateInput): Flow<com.example.ryady.network.model.Response<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCartById(id: String): Flow<com.example.ryady.network.model.Response<RetrieveCartQuery.Cart>> {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override suspend fun <T> deleteCartLine(
        cartId: String,
        lineID: String
    ): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> searchForProducts(itemName: String): Flow<com.example.ryady.network.model.Response<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchProductsByCategory(category: String): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun addItemToFavourite(email: String, product: ProductByIdQuery.Product) {
        println("called")
        favouriteList[email]?.add(product)
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

    override suspend fun createOrderInformation(token: String, order: Order) {
        TODO("Not yet implemented")
    }

    override suspend fun <T> getCustomerData(token: String): Flow<com.example.ryady.network.model.Response<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> createEmptyCart(
        email: String,
        token: String
    ): com.example.ryady.network.model.Response<T> {
        TODO("Not yet implemented")
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