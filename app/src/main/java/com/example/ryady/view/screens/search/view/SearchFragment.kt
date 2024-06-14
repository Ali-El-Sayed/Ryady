package com.example.ryady.view.screens.search.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ryady.databinding.FragmentSearchBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.search.viewModel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment(), onSearchItemClick {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchProductItemAdapter: SearchProductItemAdapter

    private val viewModel by lazy {
        val factory =
            ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[SearchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x: MutableStateFlow<String?> = MutableStateFlow(null)
        searchProductItemAdapter = SearchProductItemAdapter(listOf(), this)
        binding.rvSearchResults.adapter = searchProductItemAdapter

        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            text?.let {
                x.value = text.toString()
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.searchProduct.collectLatest { response ->
                when (response) {
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> {
                        withContext(Dispatchers.Main) {
                            if (response.data.isEmpty()) {
                                binding.imgNotFound.visibility = View.VISIBLE
                                searchProductItemAdapter.setList(response.data)
                            } else {
                                binding.imgNotFound.visibility = View.GONE
                                searchProductItemAdapter.setList(response.data)
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            x.debounce(500).collectLatest {
                it?.let {
                    viewModel.getSearchedItem(it)

                }
            }
        }
    }

    override fun onItemClick(itemId: String) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToProductInfoFragment(
                itemId
            )
        )
    }


}