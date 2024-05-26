package com.example.ryady.view.screens.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.ryady.databinding.FragmentHomeScreenBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.model.Product
import com.example.ryady.network.GraphqlClient
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.home.adapters.CarouselAdapter
import com.example.ryady.view.screens.home.adapters.ProductsAdapter
import com.example.ryady.view.screens.home.viewmodel.HomeViewModel
import com.google.android.material.carousel.CarouselSnapHelper


class HomeScreen : Fragment() {
    private val binding by lazy { FragmentHomeScreenBinding.inflate(layoutInflater) }
    private val viewmodel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }


    val discounts = mutableListOf(
        "https://wallpapertag.com/wallpaper/full/d/3/c/968676-hi-res-background-images-2651x1813-retina.jpg",
        "https://th.bing.com/th/id/OIP.cnKQ-4bc3FtM8m8An_KU-wHaDP?w=800&h=350&rs=1&pid=ImgDetMain",
        "https://th.bing.com/th/id/OIP.7TrUBGreRR4vgwoYldj8pAHaEo?rs=1&pid=ImgDetMain"
    )

    val products = mutableListOf(
        Product(
            title = "Product 1", vendor = "Vendor 1", maxPrice = "100", minPrice = "50", currency = "EGP"
        ), Product(
            title = "Product 2", vendor = "Vendor 2", maxPrice = "200", minPrice = "150", currency = "EGP"
        ),

        Product(
            title = "Product 3", vendor = "Vendor 3", maxPrice = "300", minPrice = "250", currency = "EGP"
        ), Product(
            title = "Product 4", vendor = "Vendor 4", maxPrice = "400", minPrice = "350", currency = "EGP"
        ), Product(
            title = "Product 5", vendor = "Vendor 5", maxPrice = "500", minPrice = "450", currency = "EGP"
        )
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CarouselSnapHelper().attachToRecyclerView(binding.discountCarouselRv)
        binding.discountCarouselRv.adapter = CarouselAdapter(discounts, requireContext())

        binding.productsRv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.productsRv.adapter = ProductsAdapter(products)
    }

}