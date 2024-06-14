package com.example.ryady.view.screens.cart.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.api.Optional
import com.example.RetrieveCartQuery
import com.example.ryady.databinding.FragmentCartBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.datasource.remote.util.RemoteDSUtils
import com.example.ryady.model.CustomerCartData
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.utils.readCart
import com.example.ryady.utils.saveCart
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.cart.viewModel.CartViewModel
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import com.example.type.CartLineInput
import com.google.firebase.database.FirebaseDatabase
import com.shopify.checkoutsheetkit.CheckoutException
import com.shopify.checkoutsheetkit.DefaultCheckoutEventProcessor
import com.shopify.checkoutsheetkit.ShopifyCheckoutSheetKit
import com.shopify.checkoutsheetkit.lifecycleevents.CheckoutCompletedEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class CartFragment : Fragment() {

    private val binding: FragmentCartBinding by lazy { FragmentCartBinding.inflate(layoutInflater) }
    private var oneTimer = 0
    lateinit var lines: List<CartLineInput>
    private var nList: ArrayList<RetrieveCartQuery.Node> = ArrayList()
    lateinit var buyer: RetrieveCartQuery.BuyerIdentity
    private var total: Double = 0.0
    private var taxes: Int = 0
    private lateinit var cartAdapter: CartAdapter
    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[CartViewModel::class.java]
    }
    var preLines = ArrayList<CartLineInput>()
    private val checkoutEventProcessors by lazy {
        object : DefaultCheckoutEventProcessor(requireActivity()) {
            override fun onCheckoutCanceled() {
                if (oneTimer == 1) {
                    oneTimer = 0
                    requireActivity().finish()
                } else {
                    preLines.clear()
                    nList.forEach {
                        Log.d(TAG, "onCheckoutCanceled: how many items do i have")
                        lateinit var cartLineInput: CartLineInput
                        it.merchandise.onProductVariant?.let { it1 ->
                            cartLineInput = CartLineInput(
                                merchandiseId = it1.id, quantity = Optional.present(it.quantity)
                            )
                        }
                        preLines.add(cartLineInput)
                    }
                    lines = preLines
                    Log.d(TAG, "onCheckoutToken: ${viewModel.userToken} , ${viewModel.email}")
                    lifecycleScope.launch {
                        viewModel.createCartWithLines(
                            lines, customerToken = viewModel.userToken, email = viewModel.email
                        )
                    }
                }
            }

            override fun onCheckoutCompleted(checkoutCompletedEvent: CheckoutCompletedEvent) {
                oneTimer = 1
                lifecycleScope.launch {
                    viewModel.createEmptyCart(viewModel.email, viewModel.userToken)
                }

            }

            override fun onCheckoutFailed(error: CheckoutException) {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.fetchCartById()
        }
        lifecycleScope.launch {
            viewModel.cartInfo.collectLatest { result ->
                when (result) {
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> {
                        nList.clear()
                        result.data.lines.edges.forEach { nList.add(it.node) }
                    }
                }
            }
        }
        lifecycleScope.launch {
            readCart(requireContext()) { map ->
                viewModel.cartId = map["cart id"] ?: "no cart"
                viewModel.checkoutUrl = map["checkout url"] ?: "no check check"
                viewModel.userToken = map["user token"] ?: ""
                viewModel.email = map["user email"] ?: ""
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.fetchCartById()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fun Double.roundTo2DecimalPlaces(): Double {
            return BigDecimal(this).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        }
        cartAdapter = CartAdapter(
            nodes = nList,
            viewModel = viewModel,
            passedScope = lifecycleScope,
            context = requireContext(),
        ) {
            // the onclick procedure
        }
        binding.cartRecycler.adapter = cartAdapter

        lifecycleScope.launch {
            viewModel.cartInfo.collectLatest { result ->
                when (result) {
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> {
                        Log.d(TAG, "retriving cart went fine")
                        nList.clear()
                        result.data.lines.edges.forEach {
                            nList.add(it.node)
                        }
                        viewModel.checkoutUrl = result.data.checkoutUrl.toString()
                        taxes = ((result.data.cost.totalAmount.amount.toString()
                            .toDouble() - result.data.cost.checkoutChargeAmount.amount.toString().toDouble()) * 100).toInt()

                        buyer = result.data.buyerIdentity
                        total = result.data.cost.totalAmount.amount.toString().toDouble()
                        val totalExchanged =
                            total / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                                TheExchangeRate.choosedCurrency.first
                            )!!)
                        binding.totalPrice.text =
                            totalExchanged.roundTo2DecimalPlaces().toString() + " " + TheExchangeRate.choosedCurrency.first

                        val subtotal = result.data.cost.checkoutChargeAmount.amount.toString().toDouble()
                        val subtotalExchanged =
                            subtotal / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                                TheExchangeRate.choosedCurrency.first
                            )!!)

                        binding.subtotalPrice.text =
                            subtotalExchanged.roundTo2DecimalPlaces().toString() + " " + TheExchangeRate.choosedCurrency.first
                        binding.tax.text = (totalExchanged - subtotalExchanged).roundTo2DecimalPlaces().toString()
                        cartAdapter.updateList(nList)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.updateCartItemInfo.collectLatest { result ->
                when (result) {
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> viewModel.fetchCartById()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.cartCreate.collectLatest { result ->
                when (result) {
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> {
                        saveCart(requireContext(), viewModel.cartId, viewModel.checkoutUrl)
                        viewModel.fetchCartById()
                    }
                }
            }
            lifecycleScope.launch {
                viewModel.createCartState.collectLatest { response ->
                    when (response) {
                        is Response.Error -> {}
                        is Response.Loading -> {}
                        is Response.Success -> {
                            viewModel.cartId = response.data.first
                            viewModel.checkoutUrl = response.data.second
                            saveCart(requireContext(), viewModel.cartId, viewModel.checkoutUrl)
                            // save to firebase
                            val database =
                                FirebaseDatabase.getInstance("https://ryady-bf500-default-rtdb.europe-west1.firebasedatabase.app/")
                            val customerRef = database.getReference("CustomerCart")
                            val customerCartData = CustomerCartData(viewModel.cartId, viewModel.checkoutUrl)

                            // Encode the email
                            val encodedEmail = RemoteDSUtils.encodeEmail(viewModel.email)

                            // Save the data to the database
                            customerRef.child(encodedEmail).setValue(customerCartData).addOnSuccessListener {
                                println("Data saved successfully")
                            }.addOnFailureListener {
                                println("Error saving data: ${it.message}")
                            }
                        }
                    }

                }
            }

        }

        binding.button.setOnClickListener {
            ShopifyCheckoutSheetKit.present(
                viewModel.checkoutUrl, requireActivity(), checkoutEventProcessors
            )
        }
    }

}
