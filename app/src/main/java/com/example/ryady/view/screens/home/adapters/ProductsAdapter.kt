package com.example.ryady.view.screens.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ryady.R
import com.example.ryady.databinding.ProductCardBinding
import com.example.ryady.model.Product

private const val TAG = "ProductsAdapter"

class ProductsAdapter(
    private val products: List<Product> = mutableListOf(), private val onProductClick: (id: String) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    private lateinit var binding: ProductCardBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        binding = ProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(products[position])
    }

    inner class ViewHolder(binding: ProductCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.currency.text = product.currency
            binding.price.text = product.maxPrice
            binding.productName.let {
                product.title = product.title.replace("\n", "")
                it.text = product.title
            }
            binding.productBrand.text = product.vendor
            binding.imageView.load(product.images[0].src) {
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.loading_placeholder)
            }
            binding.root.setOnClickListener {
                onProductClick(product.id)
            }
        }
    }
}
