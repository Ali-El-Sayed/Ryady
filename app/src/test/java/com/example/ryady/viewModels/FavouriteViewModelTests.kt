package com.example.ryady.viewModels

import com.example.ProductByIdQuery
import com.example.ryady.fakeRepo.FakeRemoteDataSource
import com.example.ryady.model.Product
import com.example.ryady.network.model.Response
import com.example.ryady.view.screens.favorite.ViewModel.FavouriteViewModel
import com.example.type.CurrencyCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FavouriteViewModelTests {


    private lateinit var viewModel: FavouriteViewModel
    private lateinit var fakeRemote: FakeRemoteDataSource


    @Before
    fun setUp() {
        fakeRemote = FakeRemoteDataSource()
        viewModel = FavouriteViewModel(fakeRemote)
    }

    @Test
    fun testGetAllFavouriteProductByEmail() = runBlocking(Dispatchers.Unconfined) {
        // given email and add item to fake list
        val email = "mh95568@gmail.com"
        fakeRemote.fireBaseFavouriteList[email] = mutableListOf()
        fakeRemote.fireBaseFavouriteList[email]?.addAll(
            arrayListOf(
                Product(),
                Product(),
                Product(),
                Product()
            )
        )

        // when we call it return list of favourite product
        viewModel.getAllFavouriteProduct("mh95568@gmail.com")
        delay(100)

        viewModel.productList.take(1).collectLatest { result ->
            when (result) {
                is Response.Error -> {

                }

                is Response.Loading -> {

                }

                is Response.Success -> {
                    assertEquals(4, result.data.size)
                }
            }
        }


    }

    @Test
    fun testDeleteItemFromFavouriteByEmailAndItemId() = runBlocking(Dispatchers.Unconfined) {

        // given id to get product details For specific item
        val email = "mh95568@gmail.com"
        val item1 = ProductByIdQuery.Product(
            id = "123",
            title = "title$123",
            description = "description$123",
            images = ProductByIdQuery.Images(listOf()),
            descriptionHtml = "descriptionHtml$123",
            priceRange = ProductByIdQuery.PriceRange(
                ProductByIdQuery.MaxVariantPrice(
                    123,
                    CurrencyCode.EGP
                ),
                minVariantPrice = ProductByIdQuery.MinVariantPrice(
                    amount = 123,
                    currencyCode = CurrencyCode.EGP
                )
            ),

            tags = listOf(),
            totalInventory = 123,
            vendor = "vendor$123",
            variants = ProductByIdQuery.Variants(edges = listOf(), nodes = listOf())
        )

        val item2 = ProductByIdQuery.Product(
            id = "567",
            title = "title$567",
            description = "description$567",
            images = ProductByIdQuery.Images(listOf()),
            descriptionHtml = "descriptionHtml$567",
            priceRange = ProductByIdQuery.PriceRange(
                ProductByIdQuery.MaxVariantPrice(
                    567,
                    CurrencyCode.EGP
                ),
                minVariantPrice = ProductByIdQuery.MinVariantPrice(
                    amount = 567,
                    currencyCode = CurrencyCode.EGP
                )
            ),

            tags = listOf(),
            totalInventory = 567,
            vendor = "vendor$567",
            variants = ProductByIdQuery.Variants(edges = listOf(), nodes = listOf())
        )
        fakeRemote.addItemToFavourite(email, item1)
        fakeRemote.addItemToFavourite(email, item2)


        // when we give email(key) and itemId(123) to method delete item that delete item from list of favourite
        viewModel.deleteItem(email, item1.id)
        delay(300)
        val result = fakeRemote.favouriteList[email]

        //then check map not have the item after deleted
        assertThat(result?.find { it.id == item1.id }, `is`(nullValue()))


    }

}
