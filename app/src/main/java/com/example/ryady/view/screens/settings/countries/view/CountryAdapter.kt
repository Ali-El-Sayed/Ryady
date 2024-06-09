package com.example.ryady.view.screens.settings.countries.view


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ryady.databinding.CountryCurrencyListItemBinding


const val TAG = "CountriesAdapter"

class CountryAdapter(
    private val context: Context,
    private var items: List<Pair<String, String>>
) : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CountryCurrencyListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateList(newItems: List<Pair<String, String>>) {
        items = newItems
        notifyDataSetChanged()

    }

    inner class ViewHolder(private val binding: CountryCurrencyListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<String, String>) {
            val link = ("https://flagcdn.com/w160/" + item.first + ".png")
           binding.countryItem.setOnClickListener {  }
            binding.countryItemName.text = item.second
            Glide.with(context)
                .load(link) // Replace with your image URL
                .apply(
                    RequestOptions()
                        .override(100, 50) // Set the size to 24x24
                )
                .into(binding.countryItemImage)
        }
    }
}
