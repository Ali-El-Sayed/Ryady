package com.example.ryady.view.screens.settings.countries.viewmodel

import com.example.ryady.Cart.FakeDataSource
import com.example.ryady.Cart.MainCoroutineRule
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.network.model.Response
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CountriesViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    lateinit var dataSource: IRemoteDataSource
    lateinit var viewModel: CountriesViewModel
    private lateinit var testDispatcher: TestCoroutineDispatcher


    @Before
    fun setUp() {
        dataSource = FakeDataSource()
        testDispatcher = TestCoroutineDispatcher()
        viewModel = CountriesViewModel(dataSource, testDispatcher)
    }


    @Test
    fun getCountries() = mainCoroutineRule.runBlockingTest {
        viewModel.getCountries()

        val result = viewModel.countriesInfo.first() as Response.Success

        TestCase.assertEquals(
            "Egypt", result.data.get("EG") ?: "not found"
        )
    }
}