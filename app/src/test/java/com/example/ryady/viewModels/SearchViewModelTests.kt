package com.example.ryady.viewModels

import com.example.ryady.fakeRepo.FakeRemoteDataSource
import com.example.ryady.network.model.Response
import com.example.ryady.view.screens.search.viewModel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test

class SearchViewModelTests {



    private lateinit var viewModel: SearchViewModel
    private lateinit var remote: FakeRemoteDataSource

    @Before
    fun setUp() {
        remote = FakeRemoteDataSource()
        viewModel = SearchViewModel(remote)

    }


    @Test
    fun testSearchedDataBasedOnTitle() = runBlocking(Dispatchers.Unconfined){
        viewModel.getSearchedItem("title1")
        delay(100)
        viewModel.searchProduct.take(1).collectLatest {result ->
            when(result){
                is Response.Error -> {

                }
                is Response.Loading -> {

                }
                is Response.Success -> {
                    assertThat(result.data[0].node.onProduct?.title , `is`("title1"))
                }
            }
        }
    }
}