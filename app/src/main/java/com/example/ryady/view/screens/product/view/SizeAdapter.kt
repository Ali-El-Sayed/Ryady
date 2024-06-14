package com.example.ryady.product.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.ryady.databinding.SizeItemBinding
import com.example.ryady.view.screens.product.view.IProductInfo

class SizeAdapter(private val sizeList : List<String> , private val listener : IProductInfo) : RecyclerView.Adapter<SizeAdapter.ViewHolder>() {

    private lateinit var binding: SizeItemBinding
    private var selectedSize : Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = SizeItemBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = sizeList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.tvSize.text = sizeList[position]
        if (holder.adapterPosition == selectedSize){
            holder.cardView.setCardBackgroundColor(Color.rgb(157,78,221))
            holder.tvSize.setTextColor(Color.WHITE)
        }else{
            holder.cardView.setCardBackgroundColor(Color.WHITE)
            holder.tvSize.setTextColor(Color.BLACK)
        }
        holder.itemView.setOnClickListener {
            listener.onItemSizeClick(holder.adapterPosition)
            notifyItemChanged(selectedSize)
            selectedSize = holder.adapterPosition
            notifyItemChanged(selectedSize)

        }
    }


    class ViewHolder(binding: SizeItemBinding) : RecyclerView.ViewHolder(binding.root){
        var tvSize: TextView
        var cardView:CardView
        init {
            tvSize = binding.tvSize
            cardView = binding.sizeItem
        }
    }
}