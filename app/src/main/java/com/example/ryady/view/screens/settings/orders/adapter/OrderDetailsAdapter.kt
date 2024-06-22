package com.example.ryady.view.screens.settings.orders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ryady.R
import com.example.ryady.databinding.OrderDetailsItemCardBinding
import com.example.ryady.model.Item
import com.example.ryady.model.extensions.roundTo2DecimalPlaces
import com.example.ryady.view.screens.settings.currency.TheExchangeRate

class OrderDetailsAdapter : ListAdapter<Item, OrderDetailsAdapter.ViewHolder>(ItemsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OrderDetailsItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: OrderDetailsItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.productImage.load(item.thumbnailUrl) {
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.loading_placeholder)
            }
            binding.productName.let {
                val name = item.productName.replace("\n", "")
                it.text = name
            }
            binding.productQuantity.text = "x${item.quantity}"
            val total =
                (item.price.toDouble() / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                    TheExchangeRate.chosenCurrency.first
                )!!)).roundTo2DecimalPlaces()
            binding.productPrice.text = "$$total ${TheExchangeRate.chosenCurrency.first}"
        }
    }
}



