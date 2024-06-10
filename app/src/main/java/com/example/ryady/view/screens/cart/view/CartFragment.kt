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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.RetrieveCartQuery
import com.example.ryady.databinding.FragmentCartBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.cart.viewModel.CartViewModel
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import com.example.type.CartLineInput
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class CartFragment : Fragment() {

    private val binding: FragmentCartBinding by lazy { FragmentCartBinding.inflate(layoutInflater) }

    lateinit var lines: List<CartLineInput>
    private var nlist: ArrayList<RetrieveCartQuery.Node> = ArrayList()
    lateinit var buyer: RetrieveCartQuery.BuyerIdentity
    private var total: Double = 0.0
    private var taxes: Int = 0
    private lateinit var cartAdapter: CartAdapter

    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(requireActivity(), factory)[CartViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchCartById()
            }
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
        binding.cartRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        cartAdapter = CartAdapter(
            nodes = nlist,
            viewModel = viewModel,
            passedScope = lifecycleScope,
            context = requireContext(),
            cartId = viewModel.cartId
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
                        nlist.clear()
                        result.data.lines.edges.forEach {
                            nlist.add(it.node)
                        }
                        viewModel.checkoutUrl = result.data.checkoutUrl.toString()
                        taxes = ((result.data.cost.totalAmount.amount.toString()
                            .toDouble() - result.data.cost.checkoutChargeAmount.amount.toString().toDouble()) * 100).toInt()

                        buyer = result.data.buyerIdentity
                        total = result.data.cost.totalAmount.amount.toString().toDouble()
                        val totalExchanged = total/(TheExchangeRate.currency.rates?.get("EGP")!!)*(TheExchangeRate.currency.rates?.get(TheExchangeRate.choosedCurrency.first)!!)
                        binding.totalPrice.text =
                     //       result.data.cost.totalAmount.amount.toString() + " " + result.data.cost.totalAmount.currencyCode.toString()
                            totalExchanged.roundTo2DecimalPlaces().toString() + " " + TheExchangeRate.choosedCurrency.first

                        val subtotal = result.data.cost.checkoutChargeAmount.amount.toString().toDouble()
                        val subtotalExchanged = subtotal/(TheExchangeRate.currency.rates?.get("EGP")!!)*(TheExchangeRate.currency.rates?.get(TheExchangeRate.choosedCurrency.first)!!)

                        binding.subtotalPrice.text = subtotalExchanged.roundTo2DecimalPlaces().toString() + " " + TheExchangeRate.choosedCurrency.first
                    /*    binding.tax.text = BigDecimal(
                            result.data.cost.totalAmount.amount.toString()
                                .toDouble() - result.data.cost.checkoutChargeAmount.amount.toString().toDouble()
                        ).setScale(2, RoundingMode.HALF_EVEN).toDouble().toString()  */
                        binding.tax.text = (totalExchanged-subtotalExchanged).roundTo2DecimalPlaces().toString()
                        cartAdapter.updateList(nlist)
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
                        viewModel.fetchCartById()
                    }
                }
            }
        }

        binding.button.setOnClickListener {
            findNavController().navigate(CartFragmentDirections.actionCartFragment2ToCustomerDataFragment())
        }
    }

}
