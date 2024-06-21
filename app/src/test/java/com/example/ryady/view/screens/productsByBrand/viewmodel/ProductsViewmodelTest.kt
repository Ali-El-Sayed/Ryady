package com.example.ryady.view.screens.productsByBrand.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ryady.fakeRepo.FakeRemoteDataSource
import com.example.ryady.model.Currency
import com.example.ryady.network.model.Response
import com.example.ryady.rules.MainCoroutineRule
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductsViewmodelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ProductsViewmodel
    private lateinit var remote: FakeRemoteDataSource

    @Before
    fun setUp() {
        remote = FakeRemoteDataSource()
        val date = "2024-06-20 00:00:00+00"
        val base = "USD"
        val rates = HashMap<String, Double>();
        rates["EGP"] = 30.95;
        rates["USD"] = 1.0;
        rates["EUR"] = 0.91;

        // Create Currency object
        val currency = Currency(date, base, rates);
        viewModel = ProductsViewmodel(remote, currency, Pair("EGP", "Egyptian Pound"))
    }


    @Test
    fun getProductsByCategory_success() = mainCoroutineRule.scope.runTest {
        // GIVEN
        viewModel.humanType = HumanType.MEN
        viewModel.categoryType = CategoryType.ALL
        // WHEN
        viewModel.getProductsByCategory()
        // THEN
        val state = viewModel.productsByCategoryList.value
        val p = (state as Response.Success).data.first()
        assertThat(p.tags, contains(HumanType.MEN.type))
    }

    @Test
    fun getProductsByBrandId_success() = mainCoroutineRule.scope.runTest {
        // GIVEN
        val brand = "Addidas"
        // WHEN
        viewModel.getProductsByBrandId(brand)
        // THEN
        val state = viewModel.productList.value
        val p = (state as Response.Success).data.first()
        assertThat(p.vendor, `is`(brand))
    }

}