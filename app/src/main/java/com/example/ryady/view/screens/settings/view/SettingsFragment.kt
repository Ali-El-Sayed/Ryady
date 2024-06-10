package com.example.ryady.view.screens.settings.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ryady.R
import com.example.ryady.databinding.FragmentProductInfoBinding
import com.example.ryady.databinding.FragmentSettingsBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.utils.readCountry
import com.example.ryady.utils.readCurrency
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.home.view.HomeScreenDirections
import com.example.ryady.view.screens.product.viewModel.ProductViewModel
import com.example.ryady.view.screens.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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


    }
}