package com.example.ryady.view.screens.cart.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.RetrieveCartQuery
import com.example.ryady.databinding.FragmentCartBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.cart.viewModel.CartViewModel
import com.shopify.checkoutsheetkit.CheckoutException
import com.shopify.checkoutsheetkit.DefaultCheckoutEventProcessor
import com.shopify.checkoutsheetkit.ShopifyCheckoutSheetKit
import com.shopify.checkoutsheetkit.lifecycleevents.CheckoutCompletedEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class CartFragment : Fragment() {

    lateinit var binding: FragmentCartBinding
    var cartId =
        "gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUhaVDZBVFkwN0hHQTNFOUQ0WFBQVktRMg?key=f413b421b8dfdefb0de01861ab320203"

    var checkouturl =
        "https://mad44-android-sv-1.myshopify.com/cart/c/Z2NwLWV1cm9wZS13ZXN0MTowMUhaVDZBVFkwN0hHQTNFOUQ0WFBQVktRMg?key=f413b421b8dfdefb0de01861ab320203"

    var nlist: ArrayList<RetrieveCartQuery.Node> = ArrayList()
    lateinit var buyer: RetrieveCartQuery.BuyerIdentity
    var total: Double = 0.0
    var mytax: Int = 0
    var pricessummed: Int = 0
    lateinit var myadapter: CartAdapter

    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[CartViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchCartById(cartId)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkoutEventProcessorsd = object : DefaultCheckoutEventProcessor(requireContext()) {
            override fun onCheckoutCanceled() {

            }

            override fun onCheckoutCompleted(checkoutCompletedEvent: CheckoutCompletedEvent) {

            }

            override fun onCheckoutFailed(error: CheckoutException) {}

        }

        binding.cartRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        myadapter = CartAdapter(nodes = nlist, viewModel = viewModel, passedScope = lifecycleScope, context = requireContext()) {
            // the onclick procedure
        }
        binding.cartRecycler.adapter = myadapter

        lifecycleScope.launch {
            viewModel.cartInfo.collectLatest { result ->
                when (result) {
                    is Response.Error -> {
                        Log.d(TAG, "Error retriving cart: ${result.message}")
                    }

                    is Response.Loading -> {}
                    is Response.Success -> {
                        Log.d(TAG, "retriving cart went fine")
                        nlist.clear()
                        result.data.lines.edges.forEach {
                            nlist.add(it.node)
                        }
                        checkouturl = result.data.checkoutUrl.toString()
                        mytax = ((result.data.cost.totalAmount.amount.toString()
                            .toDouble() - result.data.cost.checkoutChargeAmount.amount.toString().toDouble()) * 100).toInt()

                        buyer = result.data.buyerIdentity
                        total = result.data.cost.totalAmount.amount.toString().toDouble()
                        binding.totalPrice.text =
                            result.data.cost.totalAmount.amount.toString() + " " + result.data.cost.totalAmount.currencyCode.toString()
                        binding.subtotalPrice.text = result.data.cost.checkoutChargeAmount.amount.toString()
                        binding.tax.text = BigDecimal(
                            result.data.cost.totalAmount.amount.toString()
                                .toDouble() - result.data.cost.checkoutChargeAmount.amount.toString().toDouble()
                        ).setScale(2, RoundingMode.HALF_EVEN).toDouble().toString()
                        myadapter.updateList(nlist)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.updateCartItemInfo.collectLatest { result ->
                when (result) {
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> viewModel.fetchCartById(cartId)
                }
            }
        }

        binding.button.setOnClickListener {
            ShopifyCheckoutSheetKit.present(
                checkouturl, requireActivity(), checkoutEventProcessorsd
            )


        }
    }

}
