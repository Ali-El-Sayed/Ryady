package com.example.ryady.view.screens.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ryady.databinding.CarouselDiscountItemBinding

class CarouselAdapter(private val discounts: List<String>, private val context: Context) :
    RecyclerView.Adapter<CarouselAdapter.CarouseViewHolder>() {
    private lateinit var binding: CarouselDiscountItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouseViewHolder {
        binding = CarouselDiscountItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CarouseViewHolder(binding)
    }

    override fun getItemCount(): Int = discounts.size

    override fun onBindViewHolder(holder: CarouseViewHolder, position: Int) {
        holder.bind(discounts[position])
    }


    inner class CarouseViewHolder(binding: CarouselDiscountItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(discount: String) {
            binding.imageCarousel.load(discount) {
                crossfade(true)
                crossfade(1000)
            }
        }
    }
}