package com.example.ryady.view.screens.settings.address.view

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
import com.example.ryady.R
import com.example.ryady.databinding.FragmentAddNewAddressBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.utils.readCustomerData
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.settings.address.viewModel.AddressViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

private const val TAG = "CustomerDataFragment"

class AddNewAddressFragment : Fragment() {
    private val binding by lazy { FragmentAddNewAddressBinding.inflate(layoutInflater) }
    private val REQUEST_LOCATION_PERMISSION = 1
    private var address: Address? = null
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    private val viewModel: AddressViewModel by lazy {
        val factory = ViewModelFactory(
            RemoteDataSource.getInstance(
                GraphqlClient.apiService
            )
        )
        ViewModelProvider(this, factory)[AddressViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            readCustomerData(requireContext()) {
                viewModel.userToken = it["user token"] ?: ""
            }
        }
        lifecycleScope.launch {
            readCustomerData(requireContext()) { map ->
                binding.etCustomerEmail.text = Factory.getInstance().newEditable(map["user email"])
                binding.etCustomerFirstName.text = Factory.getInstance().newEditable(map["first name"])
                binding.etCustomerLastName.text = Factory.getInstance().newEditable(map["last name"])
            }
        }
        binding.tvGetCurrentLocation.setOnClickListener {
            if (isGPSEnabled(requireActivity())) getFreshLocation()
            else showEnableGPSDialog()
        }

        // Save button
        binding.topAppBar.menu.findItem(R.id.save).setOnMenuItemClickListener {
            // customer email
            viewModel.address.email = binding.etCustomerEmail.text.toString()
            // customer name
            viewModel.address.firstName = binding.etCustomerFirstName.text.toString()
            viewModel.address.lastName = binding.etCustomerLastName.text.toString()
            // phone number
            viewModel.address.phone = binding.etCustomerPhone.text.toString()
            viewModel.address.address = binding.etCustomerLocation.text.toString()
            viewModel.address.country = binding.etCountry.text.toString()
            viewModel.address.zip = binding.etPostalCode.text.toString()
            viewModel.address.city = binding.etCustomerCity.text.toString()

            lifecycleScope.launch { viewModel.saveAddress() }
            true
        }

        // Observe the response of the saveAddress function
        lifecycleScope.launch {
            viewModel.isAddressSaved.collectLatest {
                when (it) {
                    is Response.Error -> {}

                    is Response.Loading -> {
                        withContext(Dispatchers.Main) {
                            toggleLoadingIndicator()
                        }
                    }

                    is Response.Success -> {
                        withContext(Dispatchers.Main) {
                            toggleLoadingIndicator()
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        if (isGPSPermissionGranted(requireActivity())) {
            if (isGPSEnabled(requireActivity())) {
                toggleLoadingIndicator()
                val cancellationTokenSource = CancellationTokenSource()
                fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token
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


    @SuppressLint("MissingPermission")
    suspend fun getLocationFromCoordinates(context: Context, latitude: Double, longitude: Double) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) geocoder.getFromLocation(latitude, longitude, 1) {
                handleGeocoderResult(it)
            }
            else withContext(Dispatchers.IO) {
                geocoder.getFromLocation(latitude, longitude, 1)?.let {
                    withContext(Dispatchers.Main) { handleGeocoderResult(it) }
                }

            }

        } catch (e: IOException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleGeocoderResult(addresses: List<Address>) {
        lifecycleScope.launch {
            try {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val locationName = address.getAddressLine(0) ?: ""
                    withContext(Dispatchers.Main) {
                        binding.etCustomerLocation.text = Factory.getInstance().newEditable(locationName)
                        binding.etPostalCode.text = Factory.getInstance().newEditable(address.postalCode ?: "")
                        binding.etCountry.text = Factory.getInstance().newEditable(address.countryName ?: "")
                        binding.etCustomerCity.text = Factory.getInstance().newEditable(address.adminArea ?: "")
                        toggleLoadingIndicator()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), "Try Again", Toast.LENGTH_SHORT).show()
            }
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