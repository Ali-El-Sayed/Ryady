package com.example.ryady.datasource.remote

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.example.AddItemsToCartMutation
import com.example.CartLinesRemoveMutation
import com.example.CartLinesUpdateMutation

import com.example.CustomerAccessTokenCreateMutation
import com.example.CustomerCreateMutation
import com.example.ProductByIdQuery
import com.example.RetrieveCartQuery

import com.example.ShopifyBrandsByIdQuery
import com.example.ShopifyBrandsQuery
import com.example.ShopifyProductByCategoryTypeQuery
import com.example.ShopifyProductsQuery
import com.example.payment.PaymentCreationResult
import com.example.payment.PaymentRequest
import com.example.payment.PaymentService
import com.example.payment.RetrofitHelper
import com.example.ryady.model.extensions.toBrandsList
import com.example.ryady.model.extensions.toProductList
import com.example.ryady.network.model.Response
import com.example.type.CustomerAccessTokenCreateInput
import com.example.type.CustomerCreateInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody

private const val TAG = "RemoteDataSource"


interface IRemoteDataSource {

    suspend fun <T> fetchProducts(): Response<T>


    suspend fun fetchProductById(id: String): Flow<Response<ProductByIdQuery.Product>>

    suspend fun fetchCartById(id: String): Flow<Response<RetrieveCartQuery.Cart>>

    suspend fun <T> fetchBrands(): Response<T>

    suspend fun <T> fetchProductsByBrandId(id: String): Response<T>

    suspend fun <T> createCustomer(newCustomer: CustomerCreateInput): Response<T>

    suspend fun <T> addItemToCart(cartId: String,varientID : String,quantity : Int): Response<T>
    suspend fun <T> updateCartLine(cartId: String,lineID : String,quantity : Int): Response<T>
    suspend fun <T> deleteCartLine(cartId: String,lineID : String): Response<T>


    suspend fun <T> createAccessToken(customer : CustomerAccessTokenCreateInput) : Flow<Response<T>>

    suspend fun <T> fetchProductsByCategory(category: String): Response<T>

    suspend fun makePaymentCall(
        publicKey: String,
        clientSecret: String,
    ): Flow<retrofit2.Response<ResponseBody>>

    suspend fun createPayment(
        paymentRequest: PaymentRequest
    ): Flow<retrofit2.Response<PaymentCreationResult>>

}

@Suppress("UNCHECKED_CAST")
class RemoteDataSource private constructor(private val client: ApolloClient) : IRemoteDataSource {
    private val retrofitService : PaymentService by lazy {
        RetrofitHelper.retrofit.create(PaymentService::class.java)
    }
    companion object {
        @Volatile
        private var instance: IRemoteDataSource? = null

        fun getInstance(client: ApolloClient) = instance ?: synchronized(this) {
            instance ?: RemoteDataSource(client).also { instance = it }
        }
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
            response.hasErrors() -> Response.Error(response.errors?.first()?.message ?: "Data Not Found")
            else -> Response.Success(response.data?.products?.toProductList() as T)
        }
    }

    override suspend fun fetchProductById(id: String): Flow<Response<ProductByIdQuery.Product>> {
        client.query(ProductByIdQuery(id)).execute().data?.product?.let {
                return flow { emit(Response.Success(it)) }
            }

        return flow { emit(Response.Error("Error get data from remote")) }
    }

    override suspend fun fetchCartById(id: String): Flow<Response<RetrieveCartQuery.Cart>> {
        client.query(RetrieveCartQuery(id)).execute().data?.cart?.let {
            return flow { emit(Response.Success(it)) }
        }

        return flow { emit(Response.Error("Error get data from remote")) }
    }

    override suspend fun <T> createCustomer(newCustomer: CustomerCreateInput): Response<T> {
        val response = client.mutation(CustomerCreateMutation(newCustomer))
            .execute()



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

    override suspend fun <T> addItemToCart(
        cartId: String,
        varientID: String,
        quantity: Int
    ): Response<T> {
        val response = client.mutation(AddItemsToCartMutation(cartid = cartId, varientid = varientID, quantity = quantity))
            .execute()


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
        cartId: String,
        lineID: String,
        quantity: Int
    ): Response<T> {
        val response = client.mutation(CartLinesUpdateMutation(cartid = cartId, linetid = lineID, quantity = quantity))
            .execute()


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
        }    }

    override suspend fun <T> deleteCartLine(cartId: String, lineID: String): Response<T> {
        val response = client.mutation(CartLinesRemoveMutation(cartid = cartId, lineid = lineID))
            .execute()


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
        }      }

    override suspend fun <T> createAccessToken(customer : CustomerAccessTokenCreateInput) : Flow<Response<T>>{
        Log.i(TAG, "createAccessToken: ")
        val response = client.mutation(CustomerAccessTokenCreateMutation(customer)).execute()

        return  when{

            (response.data?.customerAccessTokenCreate?.customerUserErrors?.size ?: -1) > 0 -> {
                flow {emit(Response.Error(response.data?.customerAccessTokenCreate?.customerUserErrors?.first()?.message ?:"Error NUll"))  }
            }

            else -> {
                flow {emit(Response.Success(response.data?.customerAccessTokenCreate?.customerAccessToken?.accessToken as T))  }
            }
        }
    }
    override  suspend fun makePaymentCall(
        publicKey: String,
        clientSecret: String,
    ): Flow<retrofit2.Response<ResponseBody>> = flow {
        emit(retrofitService.getPaymentPage(publicKey,clientSecret))
    }

    override  suspend fun createPayment(
        paymentRequest: PaymentRequest
    ): Flow<retrofit2.Response<PaymentCreationResult>> = flow {
        emit(retrofitService.createPayment(paymentRequest))
    }
}
