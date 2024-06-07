package com.example.ryady.view.screens.product.view

import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ProductByIdQuery
import com.example.ryady.Variant
import com.example.ryady.databinding.FragmentProductInfoBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.screens.product.viewModel.ProductViewModel
import com.example.ryady.view.factory.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ProductInfoFragment"

class ProductInfoFragment : Fragment() {

    lateinit var binding: FragmentProductInfoBinding
    var variantId =""

    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[ProductViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductInfoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id : String = ProductInfoFragmentArgs.fromBundle(requireArguments()).productId
        Log.i(TAG, "onCreate Id: $id")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.fetchProductById(id)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.productInfo.collectLatest {
                withContext(Dispatchers.Main){
                    when(it){
                        is Response.Error -> {
                            Log.i(TAG, "onViewCreated: Error ${it.message}")
                        }
                        is Response.Loading -> {

                        }
                        is Response.Success -> {
                            Log.i(TAG, "onViewCreated: Success ${it.data.title}")
                            variantId = it.data.variants.edges.first().node.id
                            updateUi(it.data)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.addItemToCartInfo.collectLatest {
                withContext(Dispatchers.Main){
                    when(it){
                        is Response.Error -> Log.i(TAG, "onViewCreated: Error ${it.message}")
                        is Response.Loading -> {}
                        is Response.Success -> Toast.makeText(activity, "Item Added Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.addToCart.setOnClickListener {
            lifecycleScope.launch {
                    viewModel.addItemToCart(cartId = "gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUhaU0FTMTJETVhUU1NaUDIxWU5UWUIzQg?key=a1ce71f0aff6abf1558ee326266537a5", varientID = variantId, quantity = 1)
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