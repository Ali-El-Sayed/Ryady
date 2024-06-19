package com.example.ryady.viewModels

import com.example.ProductByIdQuery
import com.example.ryady.fakeRepo.FakeRemoteDataSource
import com.example.ryady.network.model.Response
import com.example.ryady.view.screens.product.viewModel.ProductViewModel
import com.example.type.CurrencyCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class ProductInfoViewModelTests {

    private lateinit var viewModel: ProductViewModel
    private lateinit var remote: FakeRemoteDataSource

    @Before
    fun setUp() {
        remote = FakeRemoteDataSource()
        viewModel = ProductViewModel(remote)

    }


    @Test
    fun testGetItemSuccessfullyById() = runBlocking(Dispatchers.Unconfined) {

        // given id to get product details For specific item
        val id = "123"

        // when we give id to method we get data fro product that match this id
        viewModel.fetchProductById(id)
        delay(100)

        // then return data successfully
        viewModel.productInfo.take(1).collectLatest { result ->
            when (result) {
                is Response.Error -> {

                }

                is Response.Loading -> {

                }

                is Response.Success -> {
                    MatcherAssert.assertThat(result.data.id, `is`("123"))
                    MatcherAssert.assertThat(result.data.title, `is`("title123"))
                    MatcherAssert.assertThat(result.data.description, `is`("description123"))
                    assertEquals(123, result.data.totalInventory)
                }
            }
        }

    }

    @Test
    fun testAddItemToFavouriteByUserEmail() = runBlocking(Dispatchers.Unconfined) {

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

        // when we give email(key) and item(value) to method we add value to list of product that match key
        viewModel.addItemToFav(email, item1)
        delay(300)
        viewModel.addItemToFav(email, item2)
        delay(300)

        //then check the product added to the email
        assertEquals(2, remote.favouriteList[email]?.size)
    }

    @Test
    fun testDeleteItemFromFavouriteByUserEmailAndItemId() = runBlocking(Dispatchers.Unconfined) {

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
        viewModel.addItemToFav(email, item1)
        delay(300)
        viewModel.addItemToFav(email, item2)
        delay(300)


        // when we give email(key) and itemId(123) to method delete item that delete item from list of favourite
        viewModel.deleteItem(email, item1.id)
        delay(300)
        val result = remote.favouriteList[email]

        //then check map not have the item after deleted
        assertEquals(null, result?.find { it.id == item1.id })

    }

    @Test
    fun testSearchForAnItemInFavouriteByUserEmailAndItemId() = runBlocking(Dispatchers.Unconfined) {

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
        viewModel.addItemToFav(email, item1)
        delay(300)
        viewModel.addItemToFav(email, item2)
        delay(300)


        // when we give email(key) and itemId(123) to method search for an item that return lambda of this item if found or not (true or false)
        var result: Boolean? = null
        viewModel.searchForAnItem(email, item1.id) {
            result = it
        }
        delay(300)

        //then check map have the item  return true
        assertEquals(true, result)



    }


}