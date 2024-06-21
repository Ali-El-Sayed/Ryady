package com.example.ryady.view.screens.cart.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.api.Optional
import com.example.RetrieveCartQuery
import com.example.ryady.R
import com.example.ryady.databinding.FragmentCartBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.model.extensions.roundTo2DecimalPlaces
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.utils.readCart
import com.example.ryady.utils.readCustomerData
import com.example.ryady.utils.saveCart
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.cart.LineItems
import com.example.ryady.view.screens.cart.viewModel.CartViewModel
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import com.example.type.CartLineInput
import com.shopify.checkoutsheetkit.CheckoutException
import com.shopify.checkoutsheetkit.DefaultCheckoutEventProcessor
import com.shopify.checkoutsheetkit.ShopifyCheckoutSheetKit
import com.shopify.checkoutsheetkit.lifecycleevents.CheckoutCompletedEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private val binding: FragmentCartBinding by lazy { FragmentCartBinding.inflate(layoutInflater) }
    private var oneTimer = 0
    private var isCash = false
    lateinit var lines: List<CartLineInput>
    private var nList: ArrayList<RetrieveCartQuery.Node> = ArrayList()
    lateinit var buyer: RetrieveCartQuery.BuyerIdentity
    private var total: Double = 0.0
    private var taxes: Int = 0
    private lateinit var cartAdapter: CartAdapter
    private val viewModel by lazy {
        val factory =
            ViewModelFactory(
                RemoteDataSource.getInstance(client = GraphqlClient.apiService)
            )
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
                        lateinit var cartLineInput: CartLineInput
                        it.merchandise.onProductVariant?.let { it1 ->
                            cartLineInput = CartLineInput(
                                merchandiseId = it1.id, quantity = Optional.present(it.quantity)
                            )
                        }
                        preLines.add(cartLineInput)
                    }
                    lines = preLines
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
                    viewModel.createEmptyCart(
                        viewModel.email, viewModel.userToken, requireContext()
                    )
                }

            }

            override fun onCheckoutFailed(error: CheckoutException) {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
                lifecycleScope.launch {
                    viewModel.fetchCartById()
                }
            }
        }
        lifecycleScope.launch {
            readCustomerData(requireContext()) { map ->
                viewModel.email = map["user email"] ?: "no email"
                viewModel.userToken = map["user token"] ?: "no token"
            }
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            viewModel.orderCreateInfo.collectLatest { result ->
                when (result) {
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> {
                        // show order completed dialog
                    }
                }
            }


            lifecycleScope.launch {
                viewModel.cartInfo.collectLatest { result ->
                    when (result) {
                        is Response.Error -> {}
                        is Response.Loading -> {
                            binding.topConstraint.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Response.Success -> {
                            nList.clear()
                            result.data.lines.edges.forEach {
                                nList.add(it.node)
                            }
                            if (nList.isEmpty()) {
                                binding.button.setBackgroundColor(
                                    resources.getColor(
                                        R.color.Gray,
                                        requireContext().theme
                                    )
                                )
                                binding.button.text = "Add items to proceed"
                                binding.button.isEnabled = false
                                binding.emptyImage.visibility = View.VISIBLE
                            } else {
                                binding.button.setBackgroundColor(
                                    resources.getColor(
                                        R.color.secondary,
                                        requireContext().theme
                                    )
                                )
                                binding.button.text = "Proceed to Payment"
                                binding.button.isEnabled = true
                                binding.emptyImage.visibility = View.GONE

                            }
                            viewModel.checkoutUrl = result.data.checkoutUrl.toString()
                            taxes = ((result.data.cost.totalAmount.amount.toString()
                                .toDouble() - result.data.cost.checkoutChargeAmount.amount.toString()
                                .toDouble()) * 100).toInt()

                            buyer = result.data.buyerIdentity
                            total = result.data.cost.totalAmount.amount.toString().toDouble()
                            val totalExchanged =
                                total / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                                    TheExchangeRate.choosedCurrency.first
                                )!!)
                            binding.totalPrice.text = totalExchanged.roundTo2DecimalPlaces()
                                .toString() + " " + TheExchangeRate.choosedCurrency.first

                            val subtotal =
                                result.data.cost.checkoutChargeAmount.amount.toString().toDouble()
                            val subtotalExchanged =
                                subtotal / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                                    TheExchangeRate.choosedCurrency.first
                                )!!)

                            binding.subtotalPrice.text = subtotalExchanged.roundTo2DecimalPlaces()
                                .toString() + " " + TheExchangeRate.choosedCurrency.first
                            binding.tax.text =
                                (totalExchanged - subtotalExchanged).roundTo2DecimalPlaces()
                                    .toString()
                            cartAdapter.updateList(nList)
                            binding.topConstraint.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
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
            }

            binding.button.setOnClickListener {
                if (isCash) {
                    var lineItemsarr = ArrayList<LineItems>()
                    lines.forEach { line ->
                        lineItemsarr.add(
                            LineItems(
                                variantId = extractNumberFromGid(line.merchandiseId),
                                quantity = line.quantity.getOrNull()
                            )
                        )
                    }


                }
            }
            ShopifyCheckoutSheetKit.present(
                viewModel.checkoutUrl, requireActivity(), checkoutEventProcessors
            )
        }
    }

    fun extractNumberFromGid(gid: String): Int {
        val regex = """(\d+)$""".toRegex()
        val matchResult = regex.find(gid)
        return matchResult?.value?.toInt()
            ?: throw IllegalArgumentException("No number found in the provided gid")
    }
}
