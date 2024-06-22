package com.example.ryady.view.screens.settings.address.viewModel

import com.example.ryady.fakeRepo.FakeRemoteDataSource
import com.example.ryady.network.model.Response
import com.example.ryady.rules.MainCoroutineRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class AddressViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AddressViewModel
    private lateinit var remote: FakeRemoteDataSource

    @Before
    fun setUp() {
        remote = FakeRemoteDataSource()
        viewModel = AddressViewModel(remote)
        viewModel.userToken = UUID.randomUUID().toString()
    }

    @Test
    fun fetchAddresses_success() = mainCoroutineRule.scope.runTest {
        // GIVEN
        val addressList = remote.addressList
        // WHEN
        viewModel.fetchAddresses()
        delay(1000)
        // THEN
        val state = viewModel.addresses.value as Response.Success
        assertThat(state.data.size, `is`(addressList.size))
    }

    @Test
    fun deleteAddress_success() = mainCoroutineRule.scope.runTest {
        launch(UnconfinedTestDispatcher()) {
            // GIVEN
            val addressList = remote.addressList
            // WHEN
            viewModel.deleteAddress(addressList[0].id)
            // THEN
            val newAddressList = remote.addressList
            assertThat(newAddressList.size, equalTo(addressList.size))
        }
    }
}