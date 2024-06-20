package com.example.ryady.fakeRepo

import com.example.ProductByIdQuery
import com.example.RetrieveCartQuery
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Address
import com.example.ryady.model.Brand
import com.example.ryady.model.Currency
import com.example.ryady.model.Product
import com.example.ryady.model.Symbols
import com.example.ryady.network.model.Response
import com.example.type.CartLineInput
import com.example.type.CurrencyCode
import com.example.type.CustomerAccessTokenCreateInput
import com.example.type.CustomerCreateInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Locale

class FakeRemoteDataSource : IRemoteDataSource {

    val favouriteList: MutableMap<String, MutableList<ProductByIdQuery.Product>> = mutableMapOf()
    override suspend fun fetchCountries(): Flow<retrofit2.Response<HashMap<String, String>>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAllCurrencies(): Flow<retrofit2.Response<Symbols>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchExchangeRates(): Flow<retrofit2.Response<Currency>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchProducts(): Response<T> = Response.Success(
        mutableListOf(
            Product("123", "title123", "description123", "Addidas", "vendor123"),
            Product("456", "title456", "description456", "Addidas", "vendor456"),
            Product("789", "title789", "description789", "Addidas", "vendor789"),
        ) as T
    )

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
                            id, CurrencyCode.EGP
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

    override suspend fun <T> createAccessToken(customer: CustomerAccessTokenCreateInput): Flow<Response<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCartById(id: String): Flow<com.example.ryady.network.model.Response<RetrieveCartQuery.Cart>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchBrands(): Response<T> = Response.Success(
        mutableListOf(
            Brand("123", "title123", "description123"),
            Brand("456", "title456", "description456"),
            Brand("789", "title789", "description789"),
        ) as T
    )

    override suspend fun <T> fetchProductsByBrandId(id: String): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> createCustomer(newCustomer: CustomerCreateInput): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> createCartWithLines(
        lines: List<CartLineInput>, customerToken: String, email: String
    ): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> addItemToCart(
        cartId: String, varientID: String, quantity: Int
    ): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> updateCartLine(
        cartId: String, lineID: String, quantity: Int
    ): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> deleteCartLine(
        cartId: String, lineID: String
    ): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> searchForProducts(itemName: String): Flow<Response<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchProductsByCategory(category: String): Response<T> {
        val products = mutableListOf(
            Product(
                vendor = "123",
                title = "title123",
                bodyHtml = "description123",
                tags = mutableListOf("men"),
                minPrice = "100",
                maxPrice = "1000"
            ), Product(
                vendor = "456",
                title = "title456",
                bodyHtml = "description456",
                tags = mutableListOf("women"),
                minPrice = "100",
                maxPrice = "1000"
            ), Product(
                vendor = "789",
                title = "title789",
                bodyHtml = "description789",
                tags = mutableListOf("kids"),
                minPrice = "100",
                maxPrice = "1000"
            ), Product(
                vendor = "123",
                title = "title123",
                bodyHtml = "description123",
                tags = mutableListOf("men"),
                minPrice = "100",
                maxPrice = "1000"
            ), Product(
                vendor = "456",
                title = "title456",
                bodyHtml = "description456",
                tags = mutableListOf("women"),
                minPrice = "100",
                maxPrice = "1000"
            ), Product(
                vendor = "789",
                title = "title789",
                bodyHtml = "description789",
                tags = mutableListOf("kids"),
                minPrice = "100",
                maxPrice = "1000"
            ), Product(
                vendor = "123",
                title = "title123",
                bodyHtml = "description123",
                tags = mutableListOf("men"),
                minPrice = "100",
                maxPrice = "1000"
            )
        )
        return if (category.isNotEmpty()) return Response.Success(products.filter { product ->
            product.tags.contains(category.lowercase(Locale.getDefault()))
        } as T) else Response.Success(products as T)
    }

    override suspend fun addItemToFavourite(email: String, product: ProductByIdQuery.Product) {
        println("called")
        favouriteList[email]?.add(product)
    }

    override suspend fun getAllFavouriteItem(
        email: String, productListL: (products: List<Product>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(email: String, itemId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun searchForAnItem(
        email: String, itemId: String, isFound: (found: Boolean) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun createAccountUsingFirebase(newCustomer: CustomerCreateInput) {
        TODO("Not yet implemented")
    }

    override suspend fun checkVerification(
        newCustomer: CustomerCreateInput, isVerified: (isVerified: Boolean) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun <T> getCustomerData(token: String): Flow<Response<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> createEmptyCart(
        email: String, token: String
    ): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchAddresses(token: String): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAddress(token: String, addressId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun <T> createUserAddress(
        token: String, address: Address
    ): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchOrders(userToken: String): Response<T> {
        TODO("Not yet implemented")
    }
}