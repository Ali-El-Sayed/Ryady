package com.example.ryady.view.screens.settings.countries.view


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ryady.databinding.CountryCurrencyListItemBinding
import com.example.ryady.utils.saveCountry
import com.example.ryady.utils.saveCurrency
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


const val TAG = "CountriesAdapter"
const val FRAGMENT_COUNTRY = 0
const val FRAGMENT_CURRENCY = 1


class CountryAdapter(
    private val context: Context,
    private var items: List<Pair<String, String>>,
    private val passedScope: CoroutineScope,
    private val navController: NavController,
    private var fragment : Int

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
            when (fragment) {
                FRAGMENT_COUNTRY -> {

                    binding.countryItem.setOnClickListener {
                        passedScope.launch {
                            saveCountry(context, item.first, item.second)
                            navController.popBackStack()
                        }
                    }
                    binding.countryItemName.text = item.second
                    Glide.with(context)
                        .load(link) // Replace with your image URL
                        .apply(
                            RequestOptions()
                                .override(100, 50) // Set the size to 24x24
                        )
                        .into(binding.countryItemImage)
                }
                FRAGMENT_CURRENCY -> {
                    binding.countryItem.setOnClickListener {
                        passedScope.launch {
                            saveCurrency(context, item.first, item.second)
                            TheExchangeRate.getSavedRate()
                            navController.popBackStack()
                        }
                    }
                    binding.countryItemName.text = item.second
                    binding.countryItemImage.visibility = View.GONE
                }
            }

        }
    }
}
