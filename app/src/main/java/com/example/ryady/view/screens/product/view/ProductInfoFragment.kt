package com.example.ryady.view.screens.product.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ProductByIdQuery
import com.example.ryady.R
import com.example.ryady.databinding.FragmentProductInfoBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.product.view.SizeAdapter
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.product.viewModel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ProductInfoFragment"

class ProductInfoFragment : Fragment() {

    lateinit var binding: FragmentProductInfoBinding
    var variantId = ""
    var isFavourite: Boolean = false
    var id: String = ""
    var cartId =
        "gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUhaVzRRUFkzVjAxMUFGNVgyVzA2MTRSQQ?key=41856e5a617ea92e991f5b9cb4dd0dd6"

    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[ProductViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        id = ProductInfoFragmentArgs.fromBundle(requireArguments()).productId
        Log.i(TAG, "onCreate Id: $id")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchProductById(id)
                viewModel.searchForAnItem(itemId = id) {
                    isFavourite = it
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.productInfo.collectLatest {
                withContext(Dispatchers.Main) {
                    when (it) {
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
                withContext(Dispatchers.Main) {
                    when (it) {
                        is Response.Error -> Log.i(TAG, "onViewCreated: Error ${it.message}")
                        is Response.Loading -> {}
                        is Response.Success -> Toast.makeText(activity, "Item Added Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.addToCart.setOnClickListener {
            lifecycleScope.launch {
                viewModel.addItemToCart(cartId, varientID = variantId, quantity = 1)
            }
        }


    }



    private fun updateUi(productInfo : ProductByIdQuery.Product){
        val productImagesUrl : MutableList<SlideModel> = mutableListOf()
        productInfo.images.edges.forEach{
            productImagesUrl.add(SlideModel(imageUrl = it.node.url.toString()))
        }
        binding.brand.text = productInfo.vendor.lowercase().replaceFirstChar {
            it.uppercase()
        }
        binding.title.text = productInfo.title
        binding.description.text = productInfo.description
        binding.price.text = productInfo.priceRange.maxVariantPrice.amount.toString()
        binding.priceUnit.text = productInfo.priceRange.maxVariantPrice.currencyCode.toString()

        binding.imageSlider.setImageList(productImagesUrl)

        binding.imageSlider.setSlideAnimation(AnimationTypes.FOREGROUND_TO_BACKGROUND)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.HORIZONTAL
        binding.sizeList.layoutManager = layoutManager
        val sizeList: MutableList<String> = mutableListOf()
        productInfo.variants.edges.forEach {
            sizeList.add(it.node.title.split("/")[0])
        }

        if (isFavourite) {
            binding.btnFavourite.setIcon(R.drawable.favorite_fill)
        } else {
            binding.btnFavourite.setIcon(R.drawable.favorite)

        }
        binding.sizeList.adapter = SizeAdapter(sizeList.toList())

        binding.btnFavourite.setOnClickListener {
            if (isFavourite) {
                viewModel.deleteItem(id)
                Toast.makeText(requireContext(), "Product Removed from Favourites", Toast.LENGTH_LONG).show()
                binding.btnFavourite.setIcon(R.drawable.favorite)
            } else {
                viewModel.addItemToFav(productInfo)
                Toast.makeText(requireContext(), "Product Added To Favourites", Toast.LENGTH_LONG).show()
                binding.btnFavourite.setIcon(R.drawable.favorite_fill)
            }

        }


    }
}