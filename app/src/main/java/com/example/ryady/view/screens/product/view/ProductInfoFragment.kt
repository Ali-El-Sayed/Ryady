package com.example.ryady.view.screens.product.view

import android.graphics.Color
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
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ProductByIdQuery
import com.example.ryady.R
import com.example.ryady.databinding.FragmentProductInfoBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.model.extensions.roundTo2DecimalPlaces
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.product.view.SizeAdapter
import com.example.ryady.utils.readCart
import com.example.ryady.utils.readCustomerData
import com.example.ryady.utils.reviews
import com.example.ryady.view.dialogs.unRegister.view.UnRegisterDialogFragment
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.product.viewModel.ProductViewModel
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode

private const val TAG = "ProductInfoFragment"

class ProductInfoFragment : Fragment(), IProductInfo {

    lateinit var binding: FragmentProductInfoBinding
    private var variantId = ""
    private var isFavourite: Boolean = false
    private lateinit var variant: ProductByIdQuery.Variants
    var id: String = ""
    lateinit var email: String
    lateinit var token: String


    private val viewModel by lazy {
        val factory =
            ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
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
        lifecycleScope.launch {
            readCart(requireContext()) { map ->
                viewModel.cartId = map["cart id"] ?: ""
            }
        }

        id = ProductInfoFragmentArgs.fromBundle(requireArguments()).productId
        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                readCustomerData(requireContext()) {
                    email = it["user email"].toString()
                    token = it["user token"] ?: ""
                    viewModel.fetchProductById(id)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.description.setCollapsedText("Read More")
        binding.description.setExpandedText("Read Less")
        binding.description.setCollapsedTextColor(R.color.secondary)
        binding.description.setExpandedTextColor(R.color.secondary)
        binding.description.setTrimLines(2)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.productInfo.collectLatest {
                withContext(Dispatchers.Main) {
                    when (it) {
                        is Response.Error -> {
                            binding.animation.visibility = View.GONE
                            Snackbar.make(binding.root, it.message, Snackbar.ANIMATION_MODE_SLIDE)
                                .show()
                        }

                        is Response.Loading -> {

                        }

                        is Response.Success -> {
                            viewModel.searchForAnItem(email = email, itemId = id) { result ->
                                isFavourite = result
                                updateUi(it.data)
                            }
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
                        is Response.Success -> Toast.makeText(
                            activity,
                            "Item Added Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.addToCart.setOnClickListener {
            if (token.isNotEmpty() || token.isNotBlank()) {
                lifecycleScope.launch {
                    viewModel.addItemToCart(viewModel.cartId, varientID = variantId, quantity = 1)
                }
            } else
                showRegisterDialog()

        }
    }

    private fun showRegisterDialog() {
        val unRegisterDialogFragment = UnRegisterDialogFragment()
        unRegisterDialogFragment.isCancelable = false
        unRegisterDialogFragment.show(
            parentFragmentManager, "unRegisterDialog"
        )
    }

    private fun updateUi(productInfo: ProductByIdQuery.Product) {
        variantId = productInfo.variants.edges.first().node.id
        variant = productInfo.variants
        val productImagesUrl: MutableList<SlideModel> = mutableListOf()
        productInfo.images.edges.forEach {
            productImagesUrl.add(SlideModel(imageUrl = it.node.url.toString()))
        }
        binding.brand.text = productInfo.vendor.lowercase().replaceFirstChar {
            it.uppercase()
        }
        binding.title.text = productInfo.title

        binding.description.text = productInfo.description
        binding.description.setTrimLines(2)

        val price = productInfo.priceRange.maxVariantPrice.amount.toString().toDouble()
        val priceExchanged =
            price / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                TheExchangeRate.chosenCurrency.first
            )!!)

        binding.price.text = priceExchanged.roundTo2DecimalPlaces().toString()
        binding.priceUnit.text = TheExchangeRate.chosenCurrency.first

        binding.imageSlider.setImageList(productImagesUrl)

        binding.imageSlider.setSlideAnimation(AnimationTypes.FOREGROUND_TO_BACKGROUND)


        val sizeList: MutableList<String> = mutableListOf()
        productInfo.variants.edges.forEach {
            sizeList.add(it.node.title.split(" /")[0])
        }
        binding.gender.text = productInfo.tags[1]
        checkEmptyInStock(0)

        val reviewList = reviews.shuffled().take(3)
        var rating = reviewList.sumOf { it.rating }.toFloat() / 3
        rating = BigDecimal(rating.toDouble()).setScale(1, RoundingMode.HALF_EVEN).toFloat()
        binding.tvRating.text = rating.toString()
        binding.rvReview.adapter = ReviewsAdapter(reviewList)

        if (isFavourite) {
            binding.btnFavourite.setIcon(R.drawable.favorite_fill)
        } else {
            binding.btnFavourite.setIcon(R.drawable.favorite)
        }
        binding.sizeList.adapter = SizeAdapter(sizeList.toList(), this)

        binding.btnFavourite.setOnClickListener {
            if (token.isNotEmpty() || token.isNotBlank()) {
                if (isFavourite) {
                    viewModel.deleteItem(email = email, id = id)
                    Toast.makeText(
                        requireContext(),
                        "Product Removed from Favourites",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnFavourite.setIcon(R.drawable.favorite)
                    isFavourite = !isFavourite
                } else {
                    viewModel.addItemToFav(email = email, productInfo)
                    Toast.makeText(
                        requireContext(),
                        "Product Added To Favourites",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    binding.btnFavourite.setIcon(R.drawable.favorite_fill)
                    isFavourite = !isFavourite
                }
            } else {
                showRegisterDialog()
            }

        }
        binding.animation.visibility = View.GONE


    }

    override fun onItemSizeClick(itemIndex: Int) {
        variantId = variant.edges[itemIndex].node.id
        checkEmptyInStock(itemIndex)
    }

    private fun checkEmptyInStock(variantIndex: Int) {
        if ((variant.edges[variantIndex].node.quantityAvailable ?: 0) <= 0) {
            binding.addToCart.text = "Sold Out"
            binding.addToCart.setTextColor(
                Color.parseColor("#FFFFFFFF")
            )
            binding.addToCart.setBackgroundColor(
                Color.parseColor("#979797")
            )
            binding.addToCart.isEnabled = false
        } else {
            binding.addToCart.text = "Add to Cart"
            binding.addToCart.setTextColor(
                Color.parseColor("#FFFFFFFF")
            )
            binding.addToCart.setBackgroundColor(
                Color.parseColor("#9D4EDD")
            )
            binding.addToCart.isEnabled = true
        }
    }
}