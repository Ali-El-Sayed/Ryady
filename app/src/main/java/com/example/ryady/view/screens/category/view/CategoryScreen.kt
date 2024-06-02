package com.example.ryady.view.screens.category.view

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
import com.example.ryady.R
import com.example.ryady.databinding.FragmentCategoryScreenBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.home.adapters.ProductsAdapter
import com.example.ryady.view.screens.productsByBrand.viewmodel.CategoryType
import com.example.ryady.view.screens.productsByBrand.viewmodel.ProductsViewmodel
import kotlinx.coroutines.launch

class CategoryScreen : Fragment() {

    private val binding by lazy { FragmentCategoryScreenBinding.inflate(layoutInflater) }
    private val viewModel: ProductsViewmodel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(GraphqlClient.apiService))
        ViewModelProvider(this, factory)[ProductsViewmodel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.getProductsByCategory(CategoryType.ALL)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                updateUI()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val categoryType = when (checkedId) {
                R.id.chip_shoes -> CategoryType.SHOES
                R.id.chip_shirts -> CategoryType.T_SHIRTS
                R.id.chip_accessories -> CategoryType.ACCESSORIES
                else -> CategoryType.ALL
            }
            lifecycleScope.launch {
                viewModel.getProductsByCategory(categoryType)
            }
        }
    }

    private suspend fun updateUI() {
        viewModel.productList.collect {
            when (it) {
                is Response.Loading -> {
                    binding.loadingBar.visibility = View.VISIBLE
                }

                is Response.Success -> {
                    binding.productsRv.layoutManager = GridLayoutManager(requireContext(), 2)
                    binding.productsRv.adapter = ProductsAdapter(it.data) { id ->
                        findNavController().navigate(
                            CategoryScreenDirections.actionCategoryScreenToProductInfoFragment(
                                productId = id
                            )
                        )
                    }
                    binding.loadingBar.visibility = View.GONE
                }

                is Response.Error -> {
                    binding.loadingBar.visibility = View.GONE

                }
            }
        }
    }
}