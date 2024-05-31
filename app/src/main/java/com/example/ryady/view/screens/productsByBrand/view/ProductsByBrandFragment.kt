package com.example.ryady.view.screens.productsByBrand.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ryady.databinding.FragmentProductsByBrandsBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.home.adapters.ProductsAdapter
import com.example.ryady.view.screens.productsByBrand.viewmodel.ProductsViewmodel
import kotlinx.coroutines.launch


class ProductsByBrandFragment : Fragment() {
    private val binding by lazy { FragmentProductsByBrandsBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(GraphqlClient.apiService))
        ViewModelProvider(this, factory)[ProductsViewmodel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString("brandId")?.let {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.getProductsByBrandId(it)
                    updateUI()
                }
            }
        } ?: {
            // Show Dialog Error and Close Fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root


    private suspend fun updateUI() {
        viewModel.productList.collect {
            when (it) {
                is Response.Loading -> {
                    // Show Progress Bar
                }

                is Response.Success -> {
                    binding.productsRv.layoutManager = GridLayoutManager(requireContext(), 2)
                    binding.productsRv.adapter = ProductsAdapter(it.data)
                    binding.topAppBar.title = it.data[0].vendor
                    binding.topAppBar.setNavigationOnClickListener { findNavController().navigateUp() }
                }

                is Response.Error -> {
                    // Show Dialog Error
                }
            }
        }
    }

}