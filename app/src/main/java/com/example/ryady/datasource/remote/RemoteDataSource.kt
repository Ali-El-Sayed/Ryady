package com.example.ryady.datasource.remote

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.AddItemsToCartMutation
import com.example.CartLinesRemoveMutation
import com.example.CartLinesUpdateMutation
import com.example.CreateAddressMutation
import com.example.CreateCartEmptyMutation
import com.example.CreateCartMutation
import com.example.CustomerAccessTokenCreateMutation
import com.example.CustomerCreateMutation
import com.example.GetCustomerDataQuery
import com.example.ProductByIdQuery
import com.example.RetrieveCartQuery
import com.example.SearchProductsQuery
import com.example.ShopifyBrandsByIdQuery
import com.example.ShopifyBrandsQuery
import com.example.ShopifyProductByCategoryTypeQuery
import com.example.ShopifyProductsQuery
import com.example.ryady.view.screens.settings.countries.RetrofitHelper
import com.example.ryady.datasource.remote.util.RemoteDSUtils.encodeEmail
import com.example.ryady.model.Currency
import com.example.ryady.model.Order
import com.example.ryady.model.Product
import com.example.ryady.model.Symbols
import com.example.ryady.model.extensions.toBrandsList
import com.example.ryady.model.extensions.toProductList
import com.example.ryady.network.model.Response
import com.example.ryady.view.screens.settings.countries.SettingsService
import com.example.ryady.view.screens.settings.currency.CurrencyRetrofitHelper
import com.example.ryady.view.screens.settings.currency.CurrencyService
import com.example.type.CartLineInput
import com.example.type.CustomerAccessTokenCreateInput
import com.example.type.CustomerCreateInput
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response as RetrofitResponse

private const val TAG = "RemoteDataSource"


interface IRemoteDataSource {

    suspend fun fetchCountries(): Flow<RetrofitResponse<HashMap<String, String>>>

    suspend fun fetchAllCurrencies(): Flow<RetrofitResponse<Symbols>>
    suspend fun fetchExchangeRates(): Flow<RetrofitResponse<Currency>>

    suspend fun <T> fetchProducts(): Response<T>
    suspend fun fetchProductById(id: String): Flow<Response<ProductByIdQuery.Product>>
    suspend fun <T> createAccessToken(customer: CustomerAccessTokenCreateInput): Flow<Response<T>>
    suspend fun fetchCartById(id: String): Flow<Response<RetrieveCartQuery.Cart>>

    suspend fun <T> fetchBrands(): Response<T>

    suspend fun <T> fetchProductsByBrandId(id: String): Response<T>

    suspend fun <T> createCustomer(newCustomer: CustomerCreateInput): Response<T>

    suspend fun <T> createCartWithLines(
        lines: List<CartLineInput>,
        customerToken: String,
        email: String
    ): Response<T>

    suspend fun <T> addItemToCart(cartId: String, varientID: String, quantity: Int): Response<T>
    suspend fun <T> updateCartLine(cartId: String, lineID: String, quantity: Int): Response<T>
    suspend fun <T> deleteCartLine(cartId: String, lineID: String): Response<T>

    suspend fun <T> searchForProducts(itemName: String): Flow<Response<T>>


    suspend fun <T> fetchProductsByCategory(category: String): Response<T>

    suspend fun addItemToFavourite(product: ProductByIdQuery.Product)

    suspend fun getAllFavouriteItem(
        email: String, productListL: (products: List<Product>) -> Unit
    )

    suspend fun deleteItem(itemId: String)


    suspend fun searchForAnItem(itemId: String, isFound: (found: Boolean) -> Unit)
    suspend fun createAccountUsingFirebase(
        newCustomer: CustomerCreateInput
    )

    suspend fun checkVerification(
        newCustomer: CustomerCreateInput, isVerified: (isVerified: Boolean) -> Unit
    )

    suspend fun createOrderInformation(token: String, order: Order)

    suspend fun <T> getCustomerData(token: String): Flow<Response<T>>
    suspend fun <T> createEmptyCart(email: String, token: String): Flow<Response<T>>


}

@Suppress("UNCHECKED_CAST")
class RemoteDataSource private constructor(private val client: ApolloClient) : IRemoteDataSource {

    private val settingsRetrofitService: SettingsService by lazy {
        RetrofitHelper.retrofit.create(SettingsService::class.java)
    }

    private val currencyRetrofitService: CurrencyService by lazy {
        CurrencyRetrofitHelper.retrofit.create(CurrencyService::class.java)
    }

    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance("https://ryady-bf500-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    companion object {
        @Volatile
        private var instance: IRemoteDataSource? = null

        fun getInstance(client: ApolloClient) = instance ?: synchronized(this) {
            instance ?: RemoteDataSource(client).also { instance = it }
        }
    }

    override suspend fun fetchCountries(): Flow<RetrofitResponse<HashMap<String, String>>> = flow {
        emit(settingsRetrofitService.getCountries())
    }

    override suspend fun fetchAllCurrencies(): Flow<retrofit2.Response<Symbols>> = flow {
        emit(currencyRetrofitService.getAllCurrencies())
    }

    override suspend fun fetchExchangeRates(): Flow<retrofit2.Response<Currency>> = flow {
        emit(currencyRetrofitService.getExchangerate())
    }


    override suspend fun <T> fetchProducts(): Response<T> {
        val response = client.query(ShopifyProductsQuery()).execute()
        return when {
            response.hasErrors() -> Response.Error(
                response.errors?.first()?.message ?: "Data Not Found"
            )

            else -> response.data?.products?.toProductList().let {
                return Response.Success(it as T)
            }
        }
    }


    override suspend fun <T> fetchBrands(): Response<T> {
        val response = client.query(ShopifyBrandsQuery()).execute()
        return when {
            response.hasErrors() -> Response.Error(
                response.errors?.first()?.message ?: "Data Not Found"
            )

            else -> response.data?.collections?.toBrandsList().let {
                return Response.Success(it as T)
            }
        }
    }

    override suspend fun <T> fetchProductsByBrandId(id: String): Response<T> {
        val response = client.query(ShopifyBrandsByIdQuery(id)).execute()
        return when {
            response.hasErrors() -> Response.Error(
                response.errors?.first()?.message ?: "Data Not Found"
            )

            else -> response.data?.collection?.products?.toProductList().let {
                it?.get(0)?.vendorImageUrl =
                    (response.data?.collection?.image?.url ?: "").toString()
                return Response.Success(it as T)
            }
        }
    }

    override suspend fun <T> fetchProductsByCategory(category: String): Response<T> {
        val response = client.query(ShopifyProductByCategoryTypeQuery(category)).execute()
        return when {
            response.hasErrors() -> Response.Error(
                response.errors?.first()?.message ?: "Data Not Found"
            )

            else -> Response.Success(response.data?.products?.toProductList() as T)
        }
    }

    override suspend fun fetchProductById(id: String): Flow<Response<ProductByIdQuery.Product>> {
        client.query(ProductByIdQuery(id)).execute().data?.product?.let {
            return flow { emit(Response.Success(it)) }
        }

        return flow { emit(Response.Error("Error get data from remote")) }
    }

    override suspend fun <T> searchForProducts(itemName: String): Flow<Response<T>> {
        val numberOfItem = Optional.present(10)
        client.query(SearchProductsQuery(itemName, numberOfItem))
            .execute().data?.search?.edges?.let {
                return flow {
                    emit(Response.Success(it as T))
                }
            }
        return flow { emit(Response.Error("data not found ")) }
    }

    override suspend fun fetchCartById(id: String): Flow<Response<RetrieveCartQuery.Cart>> {
        client.query(RetrieveCartQuery(id)).execute().data?.cart?.let {
            return flow { emit(Response.Success(it)) }
        }

        return flow { emit(Response.Error("Error get data from remote")) }
    }

    override suspend fun <T> createCustomer(newCustomer: CustomerCreateInput): Response<T> {
        val response = client.mutation(CustomerCreateMutation(newCustomer)).execute()
        return when {
            (((response.data?.customerCreate?.customerUserErrors?.size ?: -1) > 0)) -> {
                Response.Error(
                    response.data?.customerCreate?.customerUserErrors?.first()?.message
                        ?: "customer error == null"
                )
            }

            else -> {
                response.data?.customerCreate?.customer.let {
                    Response.Success(it as T)
                }
            }
        }
    }

    override suspend fun <T> createCartWithLines(
        lines: List<CartLineInput>,
        customerToken: String,
        email: String
    ): Response<T> {
        val response = client.mutation(CreateCartMutation(lines, customerToken, email)).execute()

        return Response.Success(
            Pair(
                first = response.data?.cartCreate?.cart?.id,
                second = response.data?.cartCreate?.cart?.checkoutUrl
            ) as T
        )

    }

    override suspend fun <T> addItemToCart(
        cartId: String, varientID: String, quantity: Int
    ): Response<T> {
        val response = client.mutation(
            AddItemsToCartMutation(
                cartid = cartId, varientid = varientID, quantity = quantity
            )
        ).execute()

        return when {

            (((response.data?.cartLinesAdd?.userErrors?.size ?: -1) > 0)) -> {
                Response.Error(
                    response.data?.cartLinesAdd?.userErrors?.first()?.message
                        ?: "add to cart error == null"
                )
            }

            else -> {
                Response.Success(1 as T)

            }
        }
    }

    override suspend fun <T> updateCartLine(
        cartId: String, lineID: String, quantity: Int
    ): Response<T> {
        val response = client.mutation(
            CartLinesUpdateMutation(
                cartid = cartId, linetid = lineID, quantity = quantity
            )
        ).execute()


        return when {

            (((response.data?.cartLinesUpdate?.userErrors?.size ?: -1) > 0)) -> {
                Response.Error(
                    response.data?.cartLinesUpdate?.userErrors?.first()?.message
                        ?: "add to cart error == null"
                )
            }

            else -> {
                Response.Success(1 as T)

            }
        }
    }
    override suspend fun <T> deleteCartLine(cartId: String, lineID: String): Response<T> {
        val response =
            client.mutation(CartLinesRemoveMutation(cartid = cartId, lineid = lineID)).execute()
        return when {
            (((response.data?.cartLinesRemove?.userErrors?.size ?: -1) > 0)) -> {
                Response.Error(
                    response.data?.cartLinesRemove?.userErrors?.first()?.message
                        ?: "remove from cart error == null"
                )
            }
            else -> {
                Response.Success(1 as T)
            }
        }
    }

    override suspend fun <T> createAccessToken(customer: CustomerAccessTokenCreateInput): Flow<Response<T>> {
        val response = client.mutation(CustomerAccessTokenCreateMutation(customer)).execute()

        return when {

            (response.data?.customerAccessTokenCreate?.customerUserErrors?.size ?: -1) > 0 -> {
                flow {
                    emit(
                        Response.Error(
                            response.data?.customerAccessTokenCreate?.customerUserErrors?.first()?.message
                                ?: "Error NUll"
                        )
                    )
                }
            }

            else -> {
                flow { emit(Response.Success(response.data?.customerAccessTokenCreate?.customerAccessToken?.accessToken as T)) }
            }
        }
    }

    override suspend fun <T> getCustomerData(token: String): Flow<Response<T>> {
        val response = client.query(GetCustomerDataQuery(token)).execute()
        return when {
            (response.errors?.size ?: -1) > 0 -> {
                flow { emit(Response.Error(response.errors?.first()?.message ?: "no Errors")) }
            }

            else -> {
                flow { emit(Response.Success(response.data?.customer as T)) }
            }
        }

    }


    override suspend fun addItemToFavourite(product: ProductByIdQuery.Product) {
        val parentRef = database.getReference("FavouriteCart")
        val email = "mh95568@gmail.com"
        parentRef.child(encodeEmail(email)).child(product.id).setValue(product)
    }


    override suspend fun getAllFavouriteItem(
        email: String, productListL: (products: List<Product>) -> Unit
    ) {

        val parentRef = database.getReference("FavouriteCart")
        val listProduct: MutableList<Product> = mutableListOf()

        parentRef.child(encodeEmail(email)).get().addOnSuccessListener {
            it.child("gid:").child("shopify").child("Product").children.forEach { prod ->

                val product = Product(
                    id = prod.child("id").value as String,
                    title = prod.child("title").value as String,
                    maxPrice = prod.child("priceRange").child("maxVariantPrice")
                        .child("amount").value as String,
                    priceCode = prod.child("priceRange").child("maxVariantPrice")
                        .child("currencyCode").value as String,
                    imageUrl = prod.child("images").child("edges").child("0").child("node")
                        .child("url").value as String
                )
                listProduct.add(product)
                Log.i(TAG, "getAllFavouriteItem: ${listProduct.size}")
            }
            productListL(listProduct)
        }
    }


    override suspend fun deleteItem(itemId: String) {
        val parentRef = database.getReference("FavouriteCart")
        parentRef.child(encodeEmail("mh95568@gmail.com")).child(itemId).removeValue()
    }

    override suspend fun searchForAnItem(
        itemId: String, isFound: (found: Boolean) -> Unit
    ) {
        val parentRef = database.getReference("FavouriteCart")
        parentRef.child(encodeEmail("mh95568@gmail.com")).child(itemId).get()
            .addOnSuccessListener {
                isFound(it.exists())
            }
    }


    override suspend fun createAccountUsingFirebase(
        newCustomer: CustomerCreateInput,
    ) {
        val auth = Firebase.auth

        auth.createUserWithEmailAndPassword(newCustomer.email, newCustomer.password)
            .addOnSuccessListener {
                auth.currentUser?.sendEmailVerification()
            }
    }

    override suspend fun checkVerification(
        newCustomer: CustomerCreateInput, isVerified: (isVerified: Boolean) -> Unit
    ) {
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(newCustomer.email, newCustomer.password)
            .addOnSuccessListener {
                auth.currentUser?.isEmailVerified?.let { it1 -> isVerified(it1) }
            }
    }


    override suspend fun createOrderInformation(token: String, order: Order) {
        client.mutation(
            CreateAddressMutation(
                token = "f4093054bf8cf9c70e84961dd8a27ed3",
                address = order.shippingAddress,
                firstname = order.customerFirstName,
                lastName = order.customerLastName,
                phone = order.customerPhoneNumbers,
                city = order.city,
                zip = order.postalCode,
                country = order.countryName
            )
        ).execute()
    }

    override suspend fun <T> createEmptyCart(email: String, token: String): Flow<Response<T>> {
        val response =
            client.mutation(CreateCartEmptyMutation(email = email, customerToken = token))
                .execute()

        return flow {
            emit(
                Response.Success(
                    Pair(
                        first = response.data?.cartCreate?.cart?.id,
                        second = response.data?.cartCreate?.cart?.checkoutUrl
                    ) as T
                )
            )
        }
    }

}
