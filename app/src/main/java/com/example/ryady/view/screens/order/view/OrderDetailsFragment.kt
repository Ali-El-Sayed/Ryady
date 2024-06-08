package com.example.ryady.view.screens.order.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable.Factory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.example.RetrieveCartQuery
import com.example.ryady.R
import com.example.ryady.databinding.FragmentOrderDetailsBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.cart.viewModel.CartViewModel
import com.example.type.CartLineInput
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shopify.checkoutsheetkit.CheckoutException
import com.shopify.checkoutsheetkit.DefaultCheckoutEventProcessor
import com.shopify.checkoutsheetkit.ShopifyCheckoutSheetKit
import com.shopify.checkoutsheetkit.lifecycleevents.CheckoutCompletedEvent
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class OrderDetailsFragment() : Fragment() {
    private val REQUEST_LOCATION_PERMISSION = 1
    private var address: Address? = null
    private val TAG = "OrderDetailsFragment"
    private val binding by lazy { FragmentOrderDetailsBinding.inflate(layoutInflater) }
    private val viewModel: CartViewModel by lazy {
        val factory = ViewModelFactory(
            RemoteDataSource.getInstance(
                GraphqlClient.apiService
            )
        )
        ViewModelProvider(requireActivity(), factory)[CartViewModel::class.java]
    }
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    /*+++++++++++++++Cart Details++++++++++++++++*/
    var email: String = "alielsayed99@gmail.com"
    var customerToken: String = "f4093054bf8cf9c70e84961dd8a27ed3"
    var prelines = ArrayList<CartLineInput>()
    lateinit var lines: List<CartLineInput>
    private var nlist: ArrayList<RetrieveCartQuery.Node> = ArrayList()
    private val checkoutEventProcessors by lazy {
        object : DefaultCheckoutEventProcessor(requireActivity()) {
            override fun onCheckoutCanceled() {
                prelines.clear()
                nlist.forEach {
                    lateinit var cartLineInput: CartLineInput
                    it.merchandise.onProductVariant?.let { it1 ->
                        cartLineInput = CartLineInput(
                            merchandiseId = it1.id, quantity = Optional.present(it.quantity)
                        )
                    }
                    prelines.add(cartLineInput)
                }
                lines = prelines
                lifecycleScope.launch { viewModel.createCartWithLines(lines, customerToken, email) }
            }

            override fun onCheckoutCompleted(checkoutCompletedEvent: CheckoutCompletedEvent) {}

            override fun onCheckoutFailed(error: CheckoutException) {}
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
                        nlist.clear()
                        result.data.lines.edges.forEach { nlist.add(it.node) }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get Current Location
        binding.tvGetCurrentLocation.setOnClickListener {
            toggleLoadingIndicator()
            if (isGPSEnabled(requireActivity())) getFreshLocation()
            else showEnableGPSDialog()
            toggleLoadingIndicator()
        }
        // Payment Method Spinner
        binding.spinner.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem(
                        text = context.getString(R.string.cash_on_delivery), iconRes = R.drawable.ic_cash_on_delivery
                    ), IconSpinnerItem(text = context.getString(R.string.credit_card), iconRes = R.drawable.ic_credit_card)
                )
            )
            getSpinnerRecyclerView().layoutManager = LinearLayoutManager(requireContext())
            selectItemByIndex(1) // select a default item.
            lifecycleOwner = lifecycleOwner
        }
        // Back Button
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
        // Checkout Button
        binding.btnCheckout.setOnClickListener {
            viewModel.currentOrder.shippingAddress = binding.etCustomerLocation.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.currentOrder.countryCode = address?.countryCode ?: ""
                viewModel.currentOrder.city = address?.adminArea ?: ""
                viewModel.currentOrder.postalCode = address?.postalCode ?: ""
                viewModel.currentOrder.countryName = address?.countryName ?: ""
                viewModel.createOrderInformation()
            }
            ShopifyCheckoutSheetKit.present(
                viewModel.checkoutUrl, requireActivity(), checkoutEventProcessors
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        if (isGPSPermissionGranted(requireActivity())) {
            if (isGPSEnabled(requireActivity())) {
                val cancellationTokenSource = CancellationTokenSource()
                fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        lifecycleScope.launch {
                            getLocationFromCoordinates(requireActivity(), location.latitude, location.longitude)
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Location not available", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(requireActivity(), "Failed to get location: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            } else showEnableGPSDialog()
        } else requestPermission()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getLocationFromCoordinates(context: Context, latitude: Double, longitude: Double) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            geocoder.getFromLocation(latitude, longitude, 1) {
                lifecycleScope.launch {
                    try {
                        if (it.size > 0) {
                            address = it[0]
                            val locationName = address?.getAddressLine(0) ?: ""
                            withContext(Dispatchers.Main) {
                                binding.etCustomerLocation.text = Factory.getInstance().newEditable(locationName)
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireActivity(), "Try Again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showEnableGPSDialog() {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setTitle(getString(R.string.enable_gps)).setMessage(getString(R.string.gps_is_disabled_do_you_want_to_enable_it))
            .setBackground(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.dialog_background, requireActivity().theme
                )
            ).setIcon(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_location, requireActivity().theme
                )
            ).setCancelable(false).setPositiveButton(getString(R.string.yes)) { _, _ ->
                val gpsOptionsIntent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(gpsOptionsIntent)
            }.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }.create().show()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), REQUEST_LOCATION_PERMISSION
        )
    }

    private fun isGPSPermissionGranted(context: Context): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationPermission || coarseLocationPermission
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this.context, "permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission is denied, show a message to the user or handle appropriately
            }
        }
    }

    private fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun toggleLoadingIndicator() {
        if (binding.frameLayout.visibility == View.GONE) binding.frameLayout.visibility = View.VISIBLE
        else binding.frameLayout.visibility = View.GONE
    }

}