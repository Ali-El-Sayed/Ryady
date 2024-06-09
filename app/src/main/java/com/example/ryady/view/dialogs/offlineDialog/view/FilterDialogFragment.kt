package com.example.ryady.view.dialogs.offlineDialog.view

import android.os.Bundle
import android.text.Editable.Factory
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.ryady.R
import com.example.ryady.databinding.FragmentFilterDialogBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.productsByBrand.viewmodel.CategoryType
import com.example.ryady.view.screens.productsByBrand.viewmodel.HumanType
import com.example.ryady.view.screens.productsByBrand.viewmodel.ProductsViewmodel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch


class FilterDialogFragment : BottomSheetDialogFragment() {
    private val binding: FragmentFilterDialogBinding by lazy { FragmentFilterDialogBinding.inflate(layoutInflater) }
    private val viewModel: ProductsViewmodel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(GraphqlClient.apiService))
        ViewModelProvider(requireParentFragment(), factory)[ProductsViewmodel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        binding.productsTypeChipGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.categoryType = when (checkedId) {
                R.id.chip_shoes -> CategoryType.SHOES
                R.id.chip_shirts -> CategoryType.T_SHIRTS
                R.id.chip_accessories -> CategoryType.ACCESSORIES
                else -> CategoryType.ALL
            }
        }
        binding.humanTypeChipGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.humanType = when (checkedId) {
                R.id.chip_men -> HumanType.MEN
                R.id.chip_women -> HumanType.WOMEN
                else -> HumanType.ALL
            }
        }
        binding.btnApply.setOnClickListener {
            updatePriceRange()
            parentFragment?.lifecycleScope?.launch {
                viewModel.getProductsByCategory()
            }
            dismiss()
        }
    }

    private fun updatePriceRange() {
        var min = 1.0
        var max = 0.0
        if (
            binding.etMinPrice.text.toString().isNotEmpty() &&
            binding.etMaxPrice.text.toString().isNotEmpty()
        ) {
            min = binding.etMinPrice.text.toString().toDouble()
            max = binding.etMaxPrice.text.toString().toDouble()

        }

        if (min <= max) viewModel.priceRange = Range(min, max)
        else Toast.makeText(requireActivity(), "Incorrect Range", Toast.LENGTH_SHORT).show()
    }

    private fun setupUi() {
        when (viewModel.humanType) {
            HumanType.MEN -> binding.chipMen.isChecked = true
            HumanType.WOMEN -> binding.chipWomen.isChecked = true
            HumanType.KIDS -> binding.chipKids.isChecked = true
            HumanType.ALL -> binding.chipAll.isChecked = true
        }
        when (viewModel.categoryType) {
            CategoryType.SHOES -> binding.chipShoes.isChecked = true
            CategoryType.T_SHIRTS -> binding.chipShirts.isChecked = true
            CategoryType.ACCESSORIES -> binding.chipAccessories.isChecked = true
            CategoryType.ALL -> binding.chipAll.isChecked = true
        }

        binding.etMinPrice.text = Factory.getInstance().newEditable(viewModel.priceRange.lower.toString())
        binding.etMaxPrice.text = Factory.getInstance().newEditable(viewModel.priceRange.upper.toString())
    }
}