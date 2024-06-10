package com.example.ryady.view.screens.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ryady.R
import com.example.ryady.databinding.ProductCardBinding
import com.example.ryady.model.Product
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import java.math.BigDecimal
import java.math.RoundingMode

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

    fun Double.roundTo2DecimalPlaces(): Double {
        return BigDecimal(this).setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(products[position])
    }

    inner class ViewHolder(binding: ProductCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.currency.text = TheExchangeRate.choosedCurrency.first
            val total = product.maxPrice.toString().toDouble()
            val totalExchanged = total/(TheExchangeRate.currency.rates?.get("EGP")!!)*(TheExchangeRate.currency.rates?.get(TheExchangeRate.choosedCurrency.first)!!)

            binding.price.text = totalExchanged.roundTo2DecimalPlaces().toString()
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
