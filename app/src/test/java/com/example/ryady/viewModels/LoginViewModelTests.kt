package com.example.ryady.viewModels

import com.apollographql.apollo3.api.Optional
import com.example.ryady.fakeRepo.FakeRemoteDataSource
import com.example.ryady.network.model.Response
import com.example.ryady.view.screens.auth.viewModel.LoginViewModel
import com.example.type.CustomerCreateInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LoginViewModelTests {


    private lateinit var viewModel: LoginViewModel
    private lateinit var fakeRemote: FakeRemoteDataSource


    @Before
    fun setUp() {
        fakeRemote = FakeRemoteDataSource()
        viewModel = LoginViewModel(fakeRemote)
    }

    @Test
    fun testCreateFirebaseAccount() = runBlocking(Dispatchers.Unconfined) {
        // given customer that will create in firebase
        val userAccount =
            CustomerCreateInput(
                firstName = Optional.present("Mohamed"),
                lastName = Optional.present("Hussein"),
                email = "mh95568@gmail.com",
                acceptsMarketing = Optional.present(false),
                password = "123456"
            )
        // when we call this method that take customer and send customer data to remote data source and send it to firebase
        viewModel.createAccountFirebase(userAccount = userAccount)
        delay(200)
        // then check account is created
        assertThat(
            userAccount,
            `is`(fakeRemote.createdAccount.find { it.email == userAccount.email })
        )
    }


    @Test
    fun testCheckVerificationForAccount() = runBlocking(Dispatchers.Unconfined) {
        // given customer that will create in firebase
        val userAccount =
            CustomerCreateInput(
                firstName = Optional.present("Mohamed"),
                lastName = Optional.present("Hussein"),
                email = "mh95568@gmail.com",
                acceptsMarketing = Optional.present(true),
                password = "123456"
            )
        // when we call this method that take customer and send customer data to remote data source and send it to firebase
        var result = false
        viewModel.checkVerification(userAccount = userAccount) {
            result = it
        }
        delay(200)
        Assert.assertEquals(true, result)
        // then check account is created
    }


    @Test
    fun testCreateShopfyAccount() = runBlocking(Dispatchers.Unconfined) {
        // given customer that will create in firebase
        val userAccount =
            CustomerCreateInput(
                firstName = Optional.present("Mohamed"),
                lastName = Optional.present("Hussein"),
                email = "mh95568@gmail.com",
                acceptsMarketing = Optional.present(true),
                password = "123456"
            )
        // when we call this method that take customer and send customer data to remote data source and send it to Shopfy(Backend)
        viewModel.createAccount(newCustomerAccount = userAccount)
        delay(200)
        viewModel.createdAccount.take(1).collectLatest {
            when (it) {
                is Response.Error -> {

                }

                is Response.Loading -> {

                }

                is Response.Success -> {
                    // then check account is created and getData from stateFlow check if email are equals and found it (true)
                    assertThat(userAccount.email, `is`( it.data.email))
                }
            }
        }

    }
}