package com.example.ryady.view.screens.settings.orders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ryady.R
import com.example.ryady.databinding.OrderListImgBinding
import com.example.ryady.model.Item

class ProductsImagesAdapter : ListAdapter<Item, ProductsImagesAdapter.ViewHolder>(ItemsDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OrderListImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ViewHolder(private val binding: OrderListImgBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.productImage.load(item.thumbnailUrl) {
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.loading_placeholder)
            }
        }
    }

}

class ItemsDiffUtil : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        // Check if items represent the same address by comparing their unique IDs
        return oldItem.productName == newItem.productName
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        // Check if the contents of the items are the same
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Item, newItem: Item): Any? {
        // You can return a payload to specify the changes between the old and new items
        return super.getChangePayload(oldItem, newItem)
    }
}