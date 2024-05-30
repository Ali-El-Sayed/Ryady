package com.example.ryady.product.view

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ProductByIdQuery
import com.example.ryady.R
import com.example.ryady.databinding.FragmentProductInfoBinding
import com.example.ryady.network.model.Response
import com.example.ryady.product.viewModel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ProductInfoFragment"

class ProductInfoFragment : Fragment() {

    lateinit var viewModel: ProductViewModel
    lateinit var binding: FragmentProductInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductInfoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =  ProductViewModel("gid://shopify/Product/7331203514451")
        lifecycleScope.launch {
            viewModel.productInfo.collectLatest {
                withContext(Dispatchers.Main){
                    when(it){
                        is Response.Error -> {

                        }
                        is Response.Loading -> {

                        }
                        is Response.Success -> {
                            updateUi(it.data)
                        }
                    }
                }
            }
        }
    }


    private fun updateUi(productInfo : ProductByIdQuery.Product){
        val productImagesUrl : MutableList<SlideModel> = mutableListOf()
        productInfo.images.edges.forEach{
            productImagesUrl.add(SlideModel(imageUrl = it.node.url.toString()))
        }
        binding.title.text = productInfo.title
        binding.description.text = productInfo.description
        binding.price.text = productInfo.priceRange.maxVariantPrice.amount.toString()
        binding.priceUnit.text = productInfo.priceRange.maxVariantPrice.currencyCode.toString()
        binding.imageSlider.setImageList(productImagesUrl)
        binding.imageSlider.setSlideAnimation(AnimationTypes.FOREGROUND_TO_BACKGROUND)
    }

}