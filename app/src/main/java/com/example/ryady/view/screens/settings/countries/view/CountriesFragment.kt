package com.example.ryady.view.screens.settings.countries.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ryady.databinding.FragmentCountriesBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.settings.countries.viewmodel.CountriesViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class CountriesFragment : Fragment() {

    lateinit var binding: FragmentCountriesBinding
    var prelist = ArrayList<Pair<String, String>>()
    lateinit var itemList: List<Pair<String, String>>
    lateinit var myadapter: CountryAdapter
    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[CountriesViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCountriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val x: MutableStateFlow<String?> = MutableStateFlow(null)
        itemList = prelist
        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        myadapter = CountryAdapter(requireContext(), itemList, lifecycleScope, findNavController(), FRAGMENT_COUNTRY)
        binding.rvSearchResults.adapter = myadapter
        lifecycleScope.launch {
            viewModel.getCountries()
        }
        binding.searchView.editText.doOnTextChanged { text, start, before, count ->
            text?.let {
                x.value = text.toString()
            }
        }

        binding.searchView.post {
            binding.searchView.show()
        }

        lifecycleScope.launch {
            x.debounce(500).collectLatest {
                it?.let {
                    var templist: ArrayList<Pair<String, String>> = ArrayList()
                    itemList.forEach { pair ->
                        if (pair.second.contains(it, true)) {
                            templist.add(pair)
                        }
                    }
                    myadapter.updateList(templist)
                }
            }
        }


        lifecycleScope.launch {
            viewModel.countriesInfo.collectLatest { result ->
                when (result) {
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> {
                        prelist.clear()
                        result.data.forEach { (k, v) ->
                            prelist.add(Pair(k, v))
                        }
                        itemList = prelist
                        myadapter.updateList(itemList)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.performClick()
    }
}