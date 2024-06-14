package com.example.ryady.view.screens.settings.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.GetCustomerDataQuery
import com.example.ryady.databinding.FragmentSettingsBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.utils.readCountry
import com.example.ryady.utils.readCurrency
import com.example.ryady.utils.saveUserData
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding

    private val viewModel by lazy {
        val factory =
            ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[SettingsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.countriesSection.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToCountriesFragment())
        }
        binding.currencySection.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToCurrencyFragment())
        }
        binding.aboutUsSection.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAboutUsFragment())
        }
        binding.addressSection.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAddressFragment())
        }
        binding.ordersSection.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToOrdersFragment())
        }
        var item: Pair<String, String>

        lifecycleScope.launch {
            readCountry(requireContext()) { map ->

                item = Pair(map.get("country code")!!, map.get("country name")!!)
                binding.countryText.text = item.second
                Log.d("settings", "onViewCreated: " + item.first)
                var link = "https://flagcdn.com/w160/" + item.first + ".png"
                Glide.with(requireContext())
                    .load(link) // Replace with your image URL
                    .apply(
                        RequestOptions()
                            .override(100, 50) // Set the size to 24x24
                    )
                    .into(binding.countryImage)
            }
        }
        lifecycleScope.launch {
            readCurrency(requireContext()) { map ->
                binding.currencyText.text = map.get("currency name")
            }
        }
        binding.logoutSection.setOnClickListener {
            lifecycleScope.launch {
                saveUserData(
                    requireContext(),
                    customer = GetCustomerDataQuery.Customer(
                        email = "", firstName = "", lastName = "", id = "", phone = "",
                        displayName = "", acceptsMarketing = false
                    ),
                    customerToken = ""
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Logout", Toast.LENGTH_LONG).show()
                    requireActivity().finish()
                }

            }
        }


    }
}