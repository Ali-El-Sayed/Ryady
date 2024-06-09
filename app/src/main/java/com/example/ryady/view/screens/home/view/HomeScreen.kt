package com.example.ryady.view.screens.home.view

import android.R.attr.label
import android.R.attr.text
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ryady.databinding.FragmentHomeScreenBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.home.adapters.BrandsAdapter
import com.example.ryady.view.screens.home.adapters.CarouselAdapter
import com.example.ryady.view.screens.home.adapters.ProductsAdapter
import com.example.ryady.view.screens.home.viewmodel.HomeViewModel
import com.google.android.material.carousel.CarouselSnapHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


private const val TAG = "HomeScreen"

class HomeScreen : Fragment() {
    private val binding by lazy { FragmentHomeScreenBinding.inflate(layoutInflater) }
    private val viewmodel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }
    private lateinit var inflatingUIJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.discountCarouselRv.onFlingListener = null
        CarouselSnapHelper().attachToRecyclerView(binding.discountCarouselRv)
        inflatingUIJob = lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch(Dispatchers.Main) { fetchProducts() }
                launch(Dispatchers.Main) { fetchBrands() }
            }
        }

        binding.topAppBar.menu.getItem(1).setOnMenuItemClickListener {
            findNavController().navigate(HomeScreenDirections.actionHomeScreenToCartFragment())
            true
        }

        binding.hodDealsText.setOnClickListener {
            findNavController().navigate(HomeScreenDirections.actionHomeScreenToSettingsFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productImagesUrl: MutableList<SlideModel> = mutableListOf()
        productImagesUrl.add(SlideModel(imageUrl = "https://cdn.al-ain.com/lg/images/2023/10/31/133-005843-spiro-spates-egyptian-drink-social-media_700x400.jpg"))
        productImagesUrl.add(SlideModel(imageUrl = "https://ik.imagekit.io/tijarahub/images/thumbnails/240/134/logos/14/elaraby-01.png.webp"))
        productImagesUrl.add(SlideModel(imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcRw7FKcF1XewWl87j2s57SFWNrl8uzPCwWp8ol1YUJVyoslvh1i3yQ19Rpkcc_SPCVfM&usqp=CAU"))

        binding.addImageSlider.setImageList(productImagesUrl)
        binding.addImageSlider.setItemClickListener(itemClickListener = object : ItemClickListener {
            override fun doubleClick(position: Int) {
                val clipboard: ClipboardManager? =

                    getSystemService<ClipboardManager>(requireContext(), ClipboardManager::class.java)
                val clip = ClipData.newPlainText("label", "copied")
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "voucher copied", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(position: Int) {
                val clipboard: ClipboardManager? =

                    getSystemService<ClipboardManager>(requireContext(), ClipboardManager::class.java)
                val clip = ClipData.newPlainText("label", "copied")
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "voucher copied", Toast.LENGTH_SHORT).show()
            }
        })
        binding.addImageSlider.setSlideAnimation(AnimationTypes.FOREGROUND_TO_BACKGROUND)
        binding.discountCarouselRv.adapter = CarouselAdapter(mutableListOf(), requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = binding.root

    private suspend fun fetchBrands() {
        viewmodel.brandList.collect {
            when (it) {
                is Response.Loading -> {
                    // Show Loading indicator
                }

                is Response.Success -> {
                    Log.d(TAG, "onCreate: $it")
                    binding.brandsRv.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false,
                    )
                    binding.brandsRv.adapter = BrandsAdapter(it?.data ?: emptyList()) { id ->
                        findNavController().navigate(HomeScreenDirections.actionHomeScreenToProductsByBrandFragment(brandId = id))
                    }
                }

                is Response.Error -> {
                    // Show Error Dialog
                }
            }
        }
    }

    private suspend fun fetchProducts() {
        viewmodel.productList.collect {
            when (it) {
                is Response.Loading -> {
                    // Show Loading Dialog
                }

                is Response.Success -> {
                    binding.productsRv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    binding.productsRv.adapter = ProductsAdapter(it?.data ?: mutableListOf()) { id ->
                        findNavController().navigate(HomeScreenDirections.actionHomeScreenToProductInfoFragment(productId = id))
                    }
                }

                is Response.Error -> {
                    // Show Error Dialog
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        inflatingUIJob.cancel()
    }

}
