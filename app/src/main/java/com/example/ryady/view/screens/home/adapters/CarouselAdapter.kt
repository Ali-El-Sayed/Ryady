package com.example.ryady.view.screens.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ryady.R
import com.example.ryady.databinding.CarouselDiscountItemBinding

class CarouselAdapter(
    private val discounts: List<String>,
    private val context: Context,
) :
    RecyclerView.Adapter<CarouselAdapter.CarouseViewHolder>() {
    private lateinit var binding: CarouselDiscountItemBinding
    private val errors =
        mutableListOf(
            R.drawable.ad1,
            R.drawable.ad2,
            R.drawable.ad3,
            R.drawable.ad4,
        )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CarouseViewHolder {
        binding = CarouselDiscountItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CarouseViewHolder(binding)
    }

    override fun getItemCount(): Int = errors.size

    override fun onBindViewHolder(
        holder: CarouseViewHolder,
        position: Int,
    ) {
        holder.bind("", errors[position])
    }

    inner class CarouseViewHolder(binding: CarouselDiscountItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            discount: String,
            error: Int,
        ) {
            binding.imageCarousel.load(discount) {
                crossfade(true)
                crossfade(1000)
                error(error)
            }
        }
    }
}
