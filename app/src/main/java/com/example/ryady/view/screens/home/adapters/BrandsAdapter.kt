package com.example.ryady.view.screens.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ryady.R
import com.example.ryady.databinding.BrandCardBinding
import com.example.ryady.model.Brand

private const val TAG = "BrandsAdapter"

class BrandsAdapter(
    private val brands: List<Brand> = mutableListOf(), private val onBrandClick: (id: String) -> Unit
) :
    RecyclerView.Adapter<BrandsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = BrandCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(brands[position])
    }

    override fun getItemCount(): Int = brands.size

    inner class ViewHolder(val binding: BrandCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(brand: Brand) {
            binding.brandImage.load(brand.imageUrl) {
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.loading_placeholder)
            }
            binding.root.setOnClickListener {
                onBrandClick(brand.id)
            }
        }
    }
}
