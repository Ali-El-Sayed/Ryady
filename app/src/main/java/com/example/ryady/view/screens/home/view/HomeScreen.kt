package com.example.ryady.view.screens.home.view

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
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
import com.example.ryady.R
import com.example.ryady.databinding.FragmentHomeScreenBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.utils.readCustomerData
import com.example.ryady.view.dialogs.unRegister.view.UnRegisterDialogFragment
import com.example.ryady.view.extensions.move
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.cart.OrderActivity
import com.example.ryady.view.screens.home.adapters.BrandsAdapter
import com.example.ryady.view.screens.home.adapters.CarouselAdapter
import com.example.ryady.view.screens.home.adapters.ProductsAdapter
import com.example.ryady.view.screens.home.viewmodel.HomeViewModel
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import com.google.android.material.carousel.CarouselSnapHelper
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val TAG = "HomeScreen"

class HomeScreen : Fragment() {
    private val binding by lazy { FragmentHomeScreenBinding.inflate(layoutInflater) }
    private val viewmodel by lazy {
        val factory =
            ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private lateinit var inflatingUIJob: Job
    private var userToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CarouselSnapHelper().attachToRecyclerView(binding.discountCarouselRv)
        inflatingUIJob = lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { fetchBrands() }
                launch(Dispatchers.Main) {
                    readCustomerData(requireContext()) { map ->
                        userToken = map["user token"] ?: ""
                        val userName = map["user name"] ?: "Guest"
                        binding.topAppBar.subtitle =
                            if (userToken.isEmpty()) "Guest" else userName.capitalize()
                    }
                }
                TheExchangeRate.currencyInfo.collectLatest {
                    if (it == 1) {
                        launch(Dispatchers.Main) { fetchProducts() }
                    }
                }
            }
        }
        binding.topAppBar.setOnMenuItemClickListener {
            if (userToken.isNotEmpty()) when (it.itemId) {
                R.id.favouriteFragment -> {
                    findNavController().navigate(HomeScreenDirections.actionHomeScreenToFavouriteFragment())
                    true
                }

                R.id.shopping_cart -> {
                    requireActivity().move(requireContext(), OrderActivity::class.java)
                    true
                }

                R.id.settings -> {
                    findNavController().navigate(HomeScreenDirections.actionHomeScreenToSettingsFragment())
                    true
                }

                else -> true
            }
            else {
                showRegisterDialog()
                true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productImagesUrl: MutableList<SlideModel> = mutableListOf()
        productImagesUrl.add(SlideModel(imagePath = R.drawable.ad1))
        productImagesUrl.add(SlideModel(imagePath = R.drawable.ad2))
        productImagesUrl.add(SlideModel(imagePath = R.drawable.ad3))
        productImagesUrl.add(SlideModel(imagePath = R.drawable.ad4))

        binding.addImageSlider.setImageList(productImagesUrl)
        binding.addImageSlider.setItemClickListener(itemClickListener = object : ItemClickListener {
            override fun doubleClick(position: Int) {}
            override fun onItemSelected(position: Int) {
                val clipboard: ClipboardManager? =
                    getSystemService(requireContext(), ClipboardManager::class.java)
                val clip = ClipData.newPlainText("label", "Eid24")
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "voucher copied", Toast.LENGTH_SHORT).show()
            }
        })
        binding.addImageSlider.setSlideAnimation(AnimationTypes.FOREGROUND_TO_BACKGROUND)
        binding.discountCarouselRv.adapter = CarouselAdapter(emptyList(), requireContext())
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
                    withContext(Dispatchers.Main) {
                        binding.brandsRv.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false,
                        )
                        binding.brandsRv.adapter =
                            ScaleInAnimationAdapter(BrandsAdapter(it.data) { id ->
                                findNavController().navigate(
                                    HomeScreenDirections.actionHomeScreenToProductsByBrandFragment(
                                        brandId = id
                                    )
                                )
                            })
                    }
                }

                is Response.Error -> {
                    // Show Error Dialog
                }
            }
        }
    }

    private fun showRegisterDialog() {
        val unRegisterDialogFragment = UnRegisterDialogFragment()
        unRegisterDialogFragment.isCancelable = false
        unRegisterDialogFragment.show(
            parentFragmentManager, "unRegisterDialog"
        )
    }

    private suspend fun fetchProducts() {
        viewmodel.productList.collect {
            when (it) {
                is Response.Loading -> {
                    // Show Loading Dialog
                }

                is Response.Success -> {
                    binding.productsRv.layoutManager =
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    binding.productsRv.adapter = ProductsAdapter(it.data) { id ->
                        findNavController().navigate(
                            HomeScreenDirections.actionHomeScreenToProductInfoFragment(
                                productId = id
                            )
                        )
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
