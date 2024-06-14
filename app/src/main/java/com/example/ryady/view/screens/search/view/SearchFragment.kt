package com.example.ryady.view.screens.search.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.SearchProductsQuery
import com.example.ryady.databinding.FragmentSearchBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val TAG = "SearchFragment"

class SearchFragment : Fragment(), onSearchItemClick {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchProductItemAdapter: SearchProductItemAdapter

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

        binding.searchView.editText.doOnTextChanged { text, start, before, count ->
            text?.let {
                x.value = text.toString()
            }
        }
        binding.searchView.post {
            binding.searchView.show()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            x.debounce(500).collectLatest {
                it?.let {
                    RemoteDataSource.getInstance(GraphqlClient.apiService).searchForProducts<List<SearchProductsQuery.Edge>>(it)
                        .collectLatest { response ->
                            when (response) {
                                is Response.Error -> {}
                                is Response.Loading -> {}
                                is Response.Success -> {
                                    withContext(Dispatchers.Main) {
                                        searchProductItemAdapter.setList(response.data)
                                    }
                                }
                            }

                        }
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