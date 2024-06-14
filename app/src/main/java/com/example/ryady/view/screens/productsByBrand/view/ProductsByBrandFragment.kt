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
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProductsByBrandFragment : Fragment() {
    private val binding by lazy { FragmentProductsByBrandsBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(GraphqlClient.apiService))
        ViewModelProvider(this, factory)[ProductsViewmodel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.topAppBar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString("brandId")?.let {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.getProductsByBrandId(it)
                    withContext(Dispatchers.Main) {
                        updateUI()
                    }
                }
            }
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
                    binding.productsRv.adapter = AlphaInAnimationAdapter(ProductsAdapter(it.data) { id ->
                        findNavController().navigate(
                            ProductsByBrandFragmentDirections.actionProductsByBrandFragmentToProductInfoFragment(
                                productId = id
                            )
                        )
                    })
                    binding.topAppBar.title = it.data[0].vendor
                }

                is Response.Error -> {
                    // Show Dialog Error
                }
            }
        }
    }

}