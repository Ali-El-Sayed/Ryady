package com.example.ryady.view.screens.Favourite.View

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ryady.R
import com.example.ryady.databinding.FavouriteListItemBinding
import com.example.ryady.model.Product
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FavouriteListAdapter(
    private val listProduct: MutableList<Product>,
    private val listener: IFavouriteFragment,
    private val context: Context
) : RecyclerView.Adapter<FavouriteListAdapter.ViewHolder>() {

    lateinit var binding: FavouriteListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = FavouriteListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listProduct.size

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle.text = listProduct[position].title
        holder.tvPrice.text = listProduct[position].maxPrice
        holder.tvPriceCode.text = listProduct[position].priceCode
        Glide.with(binding.root).load(listProduct[position].imageUrl).into(holder.ivProduct)
        holder.btnDelete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)

            builder.setTitle("Delete Confirmation")
                .setMessage("Are you sure to delete this product from Favourites")
                .setBackground(
                    ResourcesCompat.getDrawable(
                        context.resources, R.drawable.delete_dialog_background, context.theme
                    )
                )
                .setPositiveButton(
                    "Yes"
                ) { _, _ ->

                    listener.deleteItem(listProduct[position].id)
                    listProduct.removeAt(position)
                    notifyDataSetChanged()
                }.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.show()


        }


        holder.itemView.setOnClickListener {
            listener.onItemClick(listProduct[position].id)
        }
    }

    inner class ViewHolder(binding: FavouriteListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvTitle: TextView
        val tvPrice: TextView
        val ivProduct: ImageView
        val tvPriceCode: TextView
        val btnDelete: FloatingActionButton

        init {
            tvTitle = binding.textTitle
            tvPrice = binding.tvPrice
            tvPriceCode = binding.tvPriceUnit
            ivProduct = binding.ivShoeIcon
            btnDelete = binding.btnDelete
        }

    }


}