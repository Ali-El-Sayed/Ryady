package com.example.ryady.view.screens.cart.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.RetrieveCartQuery
import com.example.ryady.Cart.FakeDataSource
import com.example.ryady.Cart.MainCoroutineRule
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.network.model.Response
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)

class CartViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    lateinit var dataSource: IRemoteDataSource
    lateinit var viewModel: CartViewModel
    lateinit var cartInfo: MutableStateFlow<Response<RetrieveCartQuery.Cart>>
    private lateinit var testDispatcher: TestCoroutineDispatcher


    @Before
    fun setUp() {
        cartInfo = MutableStateFlow(Response.Loading())
        dataSource = FakeDataSource()
        testDispatcher = TestCoroutineDispatcher()
        viewModel = CartViewModel(dataSource, testDispatcher)
    }

    @Test
    fun fetchCartById() = mainCoroutineRule.runBlockingTest {
        viewModel.cartId =
            "gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUowUkozNFMwOUNUSDYyUU1ZWUNONVRONg?key=8e77553670b039b9a8ac563c991820ac"

        viewModel.fetchCartById()

        assertEquals(
            viewModel.checkoutUrl,
            "https://mad44-android-sv-1.myshopify.com/cart/c/Z2NwLWV1cm9wZS13ZXN0MTowMUowUkozNFMwOUNUSDYyUU1ZWUNONVRONg?key=8e77553670b039b9a8ac563c991820ac"
        )
    }

    @Test
    fun updateCartLine() = mainCoroutineRule.runBlockingTest {
        viewModel.updateCartLine(
            "gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUowUkozNFMwOUNUSDYyUU1ZWUNONVRONg?key=8e77553670b039b9a8ac563c991820ac",
            "gid://shopify/line/7448707039315",
            2
        )
        val result = viewModel.updateCartItemInfo.first() as Response.Success
        assertEquals(
            1, result.data
        )
    }

    @Test
    fun deleteCartLine() = mainCoroutineRule.runBlockingTest {
        viewModel.deleteCartLine(
            "gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUowUkozNFMwOUNUSDYyUU1ZWUNONVRONg?key=8e77553670b039b9a8ac563c991820ac",
            "gid://shopify/line/7448707039315"
        )
        val result = viewModel.updateCartItemInfo.first() as Response.Success
        assertEquals(
            1, result.data
        )
    }

    @Test
    fun createCartWithLines() = mainCoroutineRule.runBlockingTest {
        viewModel.createCartWithLines(
            emptyList(),
            "f4093054bf8cf9c70e84961dd8a27ed3", "ahmed123@gmail.com"
        )
        val result = viewModel.cartCreate.first() as Response.Success
        assertEquals(
            Pair(
                first = "idlines",
                second = "urllines"
            ), result.data
        )
    }
}