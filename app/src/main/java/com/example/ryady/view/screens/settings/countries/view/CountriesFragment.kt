package com.example.ryady.view.screens.settings.countries.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ryady.R
import com.example.ryady.databinding.FragmentCountriesBinding
import com.example.ryady.databinding.FragmentSettingsBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.cart.view.CartAdapter
import com.example.ryady.view.screens.settings.countries.viewmodel.CountriesViewModel
import com.example.ryady.view.screens.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CountriesFragment : Fragment() {

    lateinit var binding: FragmentCountriesBinding
    var prelist = ArrayList<Pair<String,String>>()
    lateinit var itemList : List<Pair<String,String>>
    lateinit var myadapter : CountryAdapter
    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[CountriesViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCountriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList= prelist
        binding.countriesRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        myadapter = CountryAdapter(requireContext(), itemList)
        binding.countriesRecycler.adapter = myadapter
        lifecycleScope.launch {
            viewModel.getCountries()
        }

        lifecycleScope.launch {
            viewModel.countriesInfo.collectLatest { result ->
                when (result) {
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> {
                        prelist.clear()
                        result.data.forEach { (k, v) ->
                            prelist.add(Pair(k,v))
                        }
                        itemList = prelist
                        myadapter.updateList(itemList)
                    }
                }
            }
        }
    }
}