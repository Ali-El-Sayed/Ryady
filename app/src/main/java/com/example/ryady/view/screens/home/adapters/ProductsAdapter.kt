package com.example.ryady.view.screens.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ryady.R
import com.example.ryady.databinding.ProdcutCardBinding
import com.example.ryady.model.Product

class ProductsAdapter(private val products: List<Product>) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    private lateinit var binding: ProdcutCardBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ProdcutCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    inner class ViewHolder(binding: ProdcutCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.currency.text = product.currency
            binding.maxPrice.text = product.maxPrice
            binding.minPrice.text = product.minPrice
            binding.productName.text = product.title
            binding.productBrand.text = product.vendor
            binding.imageView.setImageResource(R.drawable.placeholder)
        }
    }
}