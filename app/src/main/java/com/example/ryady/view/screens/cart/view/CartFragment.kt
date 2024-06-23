package com.example.ryady.view.screens.cart.view

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.example.RetrieveCartQuery
import com.example.ryady.R
import com.example.ryady.databinding.AddAddressDialogBinding
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shopify.checkoutsheetkit.CheckoutException
import com.shopify.checkoutsheetkit.DefaultCheckoutEventProcessor
import com.shopify.checkoutsheetkit.ShopifyCheckoutSheetKit
import com.shopify.checkoutsheetkit.lifecycleevents.CheckoutCompletedEvent
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private val binding: FragmentCartBinding by lazy { FragmentCartBinding.inflate(layoutInflater) }
    private var oneTimer = 0
    lateinit var lines: List<CartLineInput>
    private var nList: ArrayList<RetrieveCartQuery.Node> = ArrayList()
    lateinit var buyer: RetrieveCartQuery.BuyerIdentity
    private var total: Double = 0.0
    private var taxes: Int = 0
    private var discountCodes: ArrayList<DiscountCodes> = ArrayList()
    private lateinit var cartAdapter: CartAdapter
    private val viewModel by lazy {
        val factory = ViewModelFactory(
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
                    findNavController().navigateUp()
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                readCart(requireContext()) { map ->
                    viewModel.cartId = map["cart id"] ?: "no cart"
                    viewModel.checkoutUrl = map["checkout url"] ?: "no check check"
                    lifecycleScope.launch { viewModel.fetchCartById() }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                readCustomerData(requireContext()) { map ->
                    viewModel.email = map["user email"] ?: "no email"
                    viewModel.userToken = map["user token"] ?: "no token"
                    launch { viewModel.fetchAddresses() }
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
        )
        binding.cartRecycler.adapter = cartAdapter

        // Payment Method Spinner
        binding.spinner.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem(
                        text = context.getString(R.string.cash_on_delivery), iconRes = R.drawable.ic_cash_on_delivery
                    ), IconSpinnerItem(
                        text = context.getString(R.string.credit_card), iconRes = R.drawable.ic_credit_card
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
                    is Response.Success -> lifecycleScope.launch {
                        // Show order confirmation dialog
                        launch(Dispatchers.Main) {
                            toggleLoadingIndicator()
                            val dialog = showOrderConfirmationDialog()
                            dialog.show()
                            delay(2000)
                            dialog.dismiss()
                            delay(500)
                            findNavController().navigateUp()
                        }
                        // Delete cart items
                        launch {
                            val idList: ArrayList<String> = ArrayList()
                            nList.forEach { idList.add(it.id) }
                            viewModel.deleteCartLine(viewModel.cartId, lineID = idList)
                        }
                    }

                    else -> {}
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
                                resources.getColor(R.color.Gray, requireContext().theme)
                            )
                            binding.button.text = "Add items to proceed"
                            binding.button.isEnabled = false
                            binding.emptyImage.visibility = View.VISIBLE
                        } else {
                            binding.button.setBackgroundColor(
                                resources.getColor(R.color.secondary, requireContext().theme)
                            )
                            binding.button.text = "Proceed to Payment"
                            binding.button.isEnabled = true
                            binding.emptyImage.visibility = View.GONE

                        }
                        viewModel.checkoutUrl = result.data.checkoutUrl.toString()
                        taxes = ((result.data.cost.totalAmount.amount.toString()
                            .toDouble() - result.data.cost.checkoutChargeAmount.amount.toString().toDouble()) * 100).toInt()

                        buyer = result.data.buyerIdentity
                        total = result.data.cost.totalAmount.amount.toString().toDouble()
                        val totalExchanged =
                            total / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                                TheExchangeRate.chosenCurrency.first
                            )!!)
                        binding.totalPrice.text =
                            totalExchanged.roundTo2DecimalPlaces().toString() + " " + TheExchangeRate.chosenCurrency.first

                        val subtotal = result.data.cost.checkoutChargeAmount.amount.toString().toDouble()
                        val subtotalExchanged =
                            subtotal / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                                TheExchangeRate.chosenCurrency.first
                            )!!)

                        binding.subtotalPrice.text =
                            subtotalExchanged.roundTo2DecimalPlaces().toString() + " " + TheExchangeRate.chosenCurrency.first
                        binding.tax.text = (totalExchanged - subtotalExchanged).roundTo2DecimalPlaces().toString()
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
                val add = viewModel.addresses.value as Response.Success
                // check if user has an address
                if (add.data.isEmpty()) showAddAddressDialog()
                else {
                    val linesItem = ArrayList<LineItems>()
                    nList.forEach { line ->
                        linesItem.add(
                            LineItems(
                                variantId = extractNumberFromGid(
                                    line.merchandise.onProductVariant?.id ?: ""
                                ), quantity = line.quantity
                            )
                        )
                    }
                    // add discount code
                    val possibleVoucher = getTextFromClipboard(requireContext())
                    if (possibleVoucher != null && possibleVoucher == "Eid24") discountCodes.add(DiscountCodes(code = possibleVoucher))

                    val order = Order(
                        lineItems = linesItem,
                        email = viewModel.email,
                        billingAddress = convertToShippingAddress(add.data.first()),
                        shippingAddress = convertToShippingAddress(add.data.first()),
                        discountCodes = discountCodes
                    )
                    toggleLoadingIndicator()
                    lifecycleScope.launch { viewModel.createOrder(OrderRequest(order = order)) }
                }
            } else ShopifyCheckoutSheetKit.present(
                viewModel.checkoutUrl, requireActivity(), checkoutEventProcessors
            )
        }
    }

    private fun extractNumberFromGid(gid: String): Long {
        val regex = """(\d+)$""".toRegex()
        val matchResult = regex.find(gid)
        return matchResult?.value?.toLong() ?: throw IllegalArgumentException("No number found in the provided gid")
    }

    private fun showOrderConfirmationDialog(): Dialog {
        val dialog = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.order_confirmation_dialog).setCancelable(false)
            .setBackground(
                AppCompatResources.getDrawable(
                    requireContext(), R.drawable.verification_dialog_background
                )
            ).create()
        return dialog
    }

    private fun showAddAddressDialog() {
        val binding = AddAddressDialogBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext()).setView(binding.root).setCancelable(false).setBackground(
            AppCompatResources.getDrawable(
                requireContext(), R.drawable.verification_dialog_background
            )
        ).create()

        binding.btnAddAddress.setOnClickListener {
            findNavController().navigate(CartFragmentDirections.actionCartFragmentToAddressFragment())
            dialog.dismiss()
        }
        binding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun convertToShippingAddress(address: Address): ShippingAddress {
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

    private fun getTextFromClipboard(context: Context): String? {
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

    private fun toggleLoadingIndicator() {
        binding.frameLayout.visibility = if (binding.frameLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }
}
