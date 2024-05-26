package com.example.ryady.view.screens.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ryady.databinding.FragmentHomeScreenBinding
import com.example.ryady.view.screens.home.adapters.CarouselAdapter
import com.google.android.material.carousel.CarouselSnapHelper


class HomeScreen : Fragment() {
    private val binding by lazy { FragmentHomeScreenBinding.inflate(layoutInflater) }

    val discounts =
        mutableListOf(
            "https://wallpapertag.com/wallpaper/full/d/3/c/968676-hi-res-background-images-2651x1813-retina.jpg",
            "https://th.bing.com/th/id/OIP.cnKQ-4bc3FtM8m8An_KU-wHaDP?w=800&h=350&rs=1&pid=ImgDetMain",
            "https://th.bing.com/th/id/OIP.7TrUBGreRR4vgwoYldj8pAHaEo?rs=1&pid=ImgDetMain"
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CarouselSnapHelper().attachToRecyclerView(binding.discountCarouselRv)
        binding.discountCarouselRv.adapter = CarouselAdapter(discounts, requireContext())

    }

}