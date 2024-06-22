package com.example.ryady.view.screens.cart.view

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.example.RetrieveCartQuery
import com.example.ryady.R
import com.example.ryady.databinding.FragmentCartBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.model.Address
import com.example.ryady.model.extensions.roundTo2DecimalPlaces
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.utils.readCart
import com.example.ryady.utils.readCustomerData
import com.example.ryady.utils.saveCart
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.cart.DiscountCodes
import com.example.ryady.view.screens.cart.LineItems
import com.example.ryady.view.screens.cart.Order
import com.example.ryady.view.screens.cart.OrderRequest
import com.example.ryady.view.screens.cart.ShippingAddress
import com.example.ryady.view.screens.cart.viewModel.CartViewModel
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import com.example.type.CartLineInput
import com.shopify.checkoutsheetkit.CheckoutException
import com.shopify.checkoutsheetkit.DefaultCheckoutEventProcessor
import com.shopify.checkoutsheetkit.ShopifyCheckoutSheetKit
import com.shopify.checkoutsheetkit.lifecycleevents.CheckoutCompletedEvent
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
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
    var discountCodes: ArrayList<DiscountCodes> = ArrayList()
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
                launch {
                    viewModel.fetchAddresses()
                }

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


        //listen to address


        // Payment Method Spinner
        binding.spinner.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem(
                        text = context.getString(R.string.cash_on_delivery),
                        iconRes = R.drawable.ic_cash_on_delivery
                    ),
                    IconSpinnerItem(
                        text = context.getString(R.string.credit_card),
                        iconRes = R.drawable.ic_credit_card
                    )
                )
            )
            getSpinnerRecyclerView().layoutManager = LinearLayoutManager(requireContext())
            selectItemByIndex(1) // select a default item.
            lifecycleOwner = lifecycleOwner
        }




        lifecycleScope.launch {
            viewModel.orderCreateInfo.collectLatest { result ->
                when (result) {
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> {
                        // show order completed dialog
                        var idList: ArrayList<String> = ArrayList()
                        nList.forEach {
                            idList.add(it.id)
                        }
                        viewModel.deleteCartLine(
                            viewModel.cartId,
                            lineID = idList
                        )
                        requireActivity().finish()
                        Toast.makeText(requireContext(), "Order Completed", Toast.LENGTH_LONG)
                            .show()
                    }
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
                                TheExchangeRate.chosenCurrency.first
                            )!!)
                        binding.totalPrice.text = totalExchanged.roundTo2DecimalPlaces()
                            .toString() + " " + TheExchangeRate.chosenCurrency.first

                        val subtotal =
                            result.data.cost.checkoutChargeAmount.amount.toString().toDouble()
                        val subtotalExchanged =
                            subtotal / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                                TheExchangeRate.chosenCurrency.first
                            )!!)

                        binding.subtotalPrice.text = subtotalExchanged.roundTo2DecimalPlaces()
                            .toString() + " " + TheExchangeRate.chosenCurrency.first
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
            if (binding.spinner.selectedIndex == 0) {
                var add = viewModel.addresses.value as Response.Success
                if (add.data.isEmpty()) {
                    Toast.makeText(requireContext(), "add an address", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    var lineItemsarr = ArrayList<LineItems>()
                    nList.forEach { line ->
                        lineItemsarr.add(
                            LineItems(
                                variantId = extractNumberFromGid(
                                    line.merchandise.onProductVariant?.id ?: ""
                                ),
                                quantity = line.quantity
                            )
                        )
                    }
                    val possibleVoucher = getTextFromClipboard(requireContext())
                    if (possibleVoucher != null && possibleVoucher == "Eid24") {
                        discountCodes.add(DiscountCodes(code = possibleVoucher))
                    }

                    var order = Order(
                        lineItems = lineItemsarr,
                        email = viewModel.email,
                        billingAddress = convertToShippingAddress(add.data.first()),
                        shippingAddress = convertToShippingAddress(add.data.first()),
                        discountCodes = discountCodes
                    )
                    var orderRequest = OrderRequest(order = order)
                    lifecycleScope.launch {
                        viewModel.createOrder(orderRequest = orderRequest)
                    }
                }
            } else {
                ShopifyCheckoutSheetKit.present(
                    viewModel.checkoutUrl, requireActivity(), checkoutEventProcessors
                )
            }

        }

    }

    fun extractNumberFromGid(gid: String): Long {
        val regex = """(\d+)$""".toRegex()
        val matchResult = regex.find(gid)
        return matchResult?.value?.toLong()
            ?: throw IllegalArgumentException("No number found in the provided gid")
    }

    private fun showOrderConfirmationDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.order_confirmation_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.verification_dialog_background
            )
        )
        dialog.setCancelable(false)
        dialog.show()
    }

    fun convertToShippingAddress(address: Address): ShippingAddress {
        return ShippingAddress(
            firstName = address.firstName,
            lastName = address.lastName,
            address1 = address.address,
            phone = address.phone,
            city = address.city,
            province = null,  // Assuming the Address class does not have a province field
            country = address.country,
            zip = address.zip
        )
    }

    fun getTextFromClipboard(context: Context): String? {
        // Get the clipboard manager service
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Check if the clipboard has data
        if (clipboard.hasPrimaryClip()) {
            // Get the primary clip
            val clipData: ClipData? = clipboard.primaryClip

            // Ensure clipData is not null and contains at least one item
            if (clipData != null && clipData.itemCount > 0) {
                // Get the text from the clipboard
                val item: ClipData.Item = clipData.getItemAt(0)
                return item.text.toString()
            }
        }

        // Return null if no data is available
        return null
    }
}
