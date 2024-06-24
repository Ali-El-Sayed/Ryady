package com.example.ryady.view.screens.product.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ryady.R
import com.example.ryady.databinding.ReviewItemBinding
import com.example.ryady.model.CustomerReview

private const val TAG = "ReviewsAdapter"

class ReviewsAdapter(val data: List<CustomerReview>, val context: Context) : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    private lateinit var binding: ReviewItemBinding
    private var maleImage = true
    private var femaleImage = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ReviewItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding.customerRating.rating = data[position].rating.toFloat()
        binding.customerReview.text = data[position].review
        binding.customerName.text = data[position].name
        if (data[position].gender == "Male") {
            Log.i(TAG, "onBindViewHolder: $maleImage")
            val drawableResId = if (maleImage) R.drawable.male1 else R.drawable.male2
            maleImage = !maleImage
            binding.customerProfileImage.setImageDrawable(context.resources.getDrawable(drawableResId))
        } else {
            Log.i(TAG, "onBindViewHolder: $femaleImage")

            val drawableResId = if (femaleImage) R.drawable.female1 else R.drawable.female2
            femaleImage = !femaleImage
            binding.customerProfileImage.setImageDrawable(context.resources.getDrawable(drawableResId))

        }
    }

    override fun getItemCount(): Int = data.size


    class ViewHolder(itemView: ReviewItemBinding) : RecyclerView.ViewHolder(itemView.root) {

    }
}