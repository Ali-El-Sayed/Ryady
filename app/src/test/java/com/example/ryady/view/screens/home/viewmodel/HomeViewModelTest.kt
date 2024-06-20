package com.example.ryady.view.screens.home.viewmodel

import com.example.ryady.fakeRepo.FakeRemoteDataSource
import com.example.ryady.model.Brand
import com.example.ryady.model.Product
import com.example.ryady.network.model.Response
import com.example.ryady.rules.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class HomeViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var remote: FakeRemoteDataSource

    @Before
    fun setUp() {
        remote = FakeRemoteDataSource()
        viewModel = HomeViewModel(remote)
    }


    @Test
    fun fetchProducts_success() = mainCoroutineRule.scope.runTest {
        // GIVEN
        val product = Product("123", "title123", "description123", "Addidas", "vendor123")
        // WHEN
        // THEN
        val state = viewModel.productList.value
        val p = (state as Response.Success).data.first()
        assertThat(p.id, `is`(product.id))
        assertThat(p.title, `is`(product.title))
        assertThat(p.vendor, `is`(product.vendor))
        assertThat(p.bodyHtml, `is`(product.bodyHtml))
        assertThat(p.productType, `is`(product.productType))
    }

    @Test
    fun fetchBrands_success() = mainCoroutineRule.scope.runTest {
        // GIVEN
        val brand = Brand("123", "title123", "description123")
        // WHEN
        // THEN
        val state = viewModel.brandList.value
        val b = (state as Response.Success).data.first()
        assertThat(b.id, `is`(brand.id))
        assertThat(b.title, `is`(brand.title))
        assertThat(b.imageUrl, `is`(brand.imageUrl))
    }
}