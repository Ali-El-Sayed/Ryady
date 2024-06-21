package com.example.ryady.fakeRepo

import com.example.CustomerCreateMutation
import com.example.ProductByIdQuery
import com.example.RetrieveCartQuery
import com.example.SearchProductsQuery
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Address
import com.example.ryady.model.Currency
import com.example.ryady.model.Product
import com.example.ryady.model.Symbols
import com.example.ryady.network.model.Response
import com.example.type.CartLineInput
import com.example.type.CurrencyCode
import com.example.type.CustomerAccessTokenCreateInput
import com.example.type.CustomerCreateInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRemoteDataSource : IRemoteDataSource {

    val favouriteList: MutableMap<String, MutableList<ProductByIdQuery.Product>> = mutableMapOf()
    val fireBaseFavouriteList: MutableMap<String, MutableList<Product>> = mutableMapOf()
    val createdAccount: MutableList<CustomerCreateInput> = mutableListOf()
    override suspend fun fetchCountries(): Flow<retrofit2.Response<HashMap<String, String>>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAllCurrencies(): Flow<retrofit2.Response<Symbols>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchExchangeRates(): Flow<retrofit2.Response<Currency>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchProducts(): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchProductById(id: String): Flow<Response<ProductByIdQuery.Product>> {
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
                        ), minVariantPrice = ProductByIdQuery.MinVariantPrice(
                            amount = id, currencyCode = CurrencyCode.EGP
                        )
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

    override suspend fun fetchCartById(id: String): Flow<Response<RetrieveCartQuery.Cart>> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchBrands(): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> fetchProductsByBrandId(id: String): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> createCustomer(newCustomer: CustomerCreateInput): Response<T> {
        val customer = CustomerCreateMutation.Customer(
            firstName = newCustomer.firstName.getOrNull(),
            lastName = newCustomer.lastName.getOrNull(),
            email = newCustomer.email,
            acceptsMarketing = newCustomer.acceptsMarketing.getOrNull() ?: true,
            displayName = "${newCustomer.firstName} ${newCustomer.lastName}" ,
            phone = "011111",
            id = newCustomer.email
        )
        return Response.Success(customer as T)
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
        return flow {
            emit(
                Response.Success(
                    listOf(
                        SearchProductsQuery.Edge(
                            node = SearchProductsQuery.Node(
                                __typename = "",
                                onProduct = SearchProductsQuery.OnProduct(
                                    id = "123",
                                    title = "title1",
                                    description = "description1",
                                    totalInventory = 123,
                                    variants = SearchProductsQuery.Variants(listOf()),
                                    images = SearchProductsQuery.Images(listOf())

                                )
                            )
                        )
                    ) as T
                )
            )
        }
    }

    override suspend fun <T> fetchProductsByCategory(category: String): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun addItemToFavourite(email: String, product: ProductByIdQuery.Product) {

        if (favouriteList[email] == null) {
            favouriteList[email] = mutableListOf(product)
        } else {
            favouriteList[email]?.add(product)
        }
    }

    override suspend fun getAllFavouriteItem(
        email: String, productListL: (products: List<Product>) -> Unit
    ) {
        productListL(fireBaseFavouriteList[email]?.toList() ?: listOf())
    }

    override suspend fun deleteItem(email: String, itemId: String) {
        favouriteList[email]?.removeIf {
            it.id == itemId
        }
    }

    override suspend fun searchForAnItem(
        email: String, itemId: String, isFound: (found: Boolean) -> Unit
    ) {
        favouriteList[email]?.forEach { product ->
            if (product.id == itemId) {
                isFound(true)
                return
            }
        }
        isFound(false)
    }

    override suspend fun createAccountUsingFirebase(newCustomer: CustomerCreateInput) {
        createdAccount.add(newCustomer)
    }

    override suspend fun checkVerification(
        newCustomer: CustomerCreateInput, isVerified: (isVerified: Boolean) -> Unit
    ) {
        if (newCustomer.acceptsMarketing.getOrNull()!!) {
            isVerified(true)
        } else {
            isVerified(false)
        }
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