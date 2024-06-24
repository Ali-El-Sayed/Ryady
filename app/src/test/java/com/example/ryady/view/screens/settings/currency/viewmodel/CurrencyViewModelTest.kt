package com.example.ryady.view.screens.settings.currency.viewmodel

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


class CurrencyViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    lateinit var dataSource: IRemoteDataSource
    lateinit var viewModel: CurrencyViewModel
    private lateinit var testDispatcher: TestCoroutineDispatcher


    @Before
    fun setUp() {
        dataSource = FakeDataSource()
        testDispatcher = TestCoroutineDispatcher()
        viewModel = CurrencyViewModel(dataSource, testDispatcher)
    }

    @Test
    fun getCurrencies() = mainCoroutineRule.runBlockingTest {
        viewModel.getCurrencies()

        val result = viewModel.currenciesInfo.first() as Response.Success

        TestCase.assertEquals(
            "Egyptian pound", result.data.currencySymbols?.get("EGP") ?: "not found"
        )
    }

    @Test
    fun getExchange() = mainCoroutineRule.runBlockingTest {
        viewModel.getExchange()

        val result = viewModel.exchangeInfo.first() as Response.Success

        TestCase.assertEquals(
            30.95, result.data.rates?.get("EGP") ?: 10.5
        )
    }
}