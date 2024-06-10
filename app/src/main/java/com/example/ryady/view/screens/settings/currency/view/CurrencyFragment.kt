package com.example.ryady.view.screens.settings.currency.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ryady.R
import com.example.ryady.databinding.FragmentCountriesBinding
import com.example.ryady.databinding.FragmentCurrencyBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.settings.countries.view.CountryAdapter
import com.example.ryady.view.screens.settings.countries.view.FRAGMENT_COUNTRY
import com.example.ryady.view.screens.settings.countries.view.FRAGMENT_CURRENCY
import com.example.ryady.view.screens.settings.countries.viewmodel.CountriesViewModel
import com.example.ryady.view.screens.settings.currency.viewmodel.CurrencyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CurrencyFragment : Fragment() {

    lateinit var binding: FragmentCurrencyBinding
    var prelist = ArrayList<Pair<String,String>>()
    lateinit var itemList : List<Pair<String,String>>
    lateinit var myadapter : CountryAdapter
    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[CurrencyViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList= prelist
        binding.CurrencyRecycle.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        myadapter = CountryAdapter(requireContext(), itemList,lifecycleScope,findNavController(), FRAGMENT_CURRENCY)
        binding.CurrencyRecycle.adapter = myadapter
        lifecycleScope.launch {
            viewModel.getCurrencies()
        }

        lifecycleScope.launch {
            viewModel.currenciesInfo.collectLatest { result ->
                when (result) {
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> {
                        prelist.clear()
                        result.data.currencySymbols?.forEach { (k, v) ->
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