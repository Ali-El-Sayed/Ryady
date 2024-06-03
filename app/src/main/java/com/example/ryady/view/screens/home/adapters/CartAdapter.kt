package com.example.ryady.view.screens.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.RetrieveCartQuery
import com.example.ryady.R
import com.example.ryady.databinding.CartListItemBinding

class CartAdapter(
    private val nodes: List<RetrieveCartQuery.Node>,
    private val onMerchandiseClick: (id: String) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CartListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = nodes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(nodes[position])
    }

    inner class ViewHolder(private val binding: CartListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(node: RetrieveCartQuery.Node) {
            binding.favView.setOnLongClickListener {
                binding.checkb.visibility = View.VISIBLE
                true
            }

            binding.buttonIncrement.setOnClickListener {
                var curr = binding.textCount.text.toString().toInt()
                curr++
                binding.textCount.text = curr.toString()
            }

            binding.buttonDecrement.setOnClickListener {
                var curr = binding.textCount.text.toString().toInt()
                if (curr > 0) curr--
                binding.textCount.text = curr.toString()
            }

            binding.cartProductTitle.text = node.merchandise.onProductVariant?.title
            binding.cartProductBrand.text = node.merchandise.onProductVariant?.barcode
            binding.textCount.text = node.quantity.toString()
            val priceText = "${node.cost.totalAmount.amount} ${node.merchandise.onProductVariant?.price?.currencyCode}"
            binding.price.text = priceText
            binding.imageView.load(node.merchandise.onProductVariant?.image?.src) {
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.placeholder)
            }

            itemView.setOnClickListener {
                node.id?.let { id -> onMerchandiseClick(id) }
            }
        }
    }
}
