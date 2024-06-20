package com.example.ryady.view.screens.settings.orders.adapter

import android.animation.LayoutTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.example.ryady.R
import com.example.ryady.databinding.OrderCardBinding
import com.example.ryady.model.Order
import com.example.ryady.model.extensions.roundTo2DecimalPlaces
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class OrdersAdapter(val onItemClicked: (Order) -> Unit) : ListAdapter<Order, OrdersAdapter.ViewHolder>(OrdersDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OrderCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: OrderCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.hiddenItems?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)
            // Set click listener for the order ID
            binding.orderId.setOnClickListener { onItemClicked(order) }
            binding.orderId.text = order.orderName

            binding.arrowButton.setOnClickListener {
                if (binding.hiddenItems.visibility == View.VISIBLE) {
                    binding.hiddenItems.visibility = View.GONE
                    binding.arrowButton.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    binding.hiddenItems.visibility = View.VISIBLE
                    binding.arrowButton.setImageResource(R.drawable.ic_arrow_up)
                }
                TransitionManager.beginDelayedTransition(binding.mainLayout, getCustomTransition())
            }

            binding.orderDate.text = formatDateString(order.processedAt)
            binding.orderItemCount.text = "${order.items.size} Items"
            val total =
                (order.totalPrice.toDouble() / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                    TheExchangeRate.chosenCurrency.first
                )!!)).roundTo2DecimalPlaces()
            binding.totalPrice.text = "$$total ${TheExchangeRate.chosenCurrency.first}"
            binding.clientName.text = "${order.address.firstName} ${order.address.lastName}".capitalize()

            val adapter = ProductsImagesAdapter()
            adapter.submitList(order.items)
            binding.orderListImg.adapter = adapter
        }
    }

    private fun getCustomTransition(): TransitionSet {
        val transitionSet = TransitionSet()
        transitionSet.ordering = TransitionSet.ORDERING_TOGETHER

        val changeBounds = ChangeBounds()
        changeBounds.duration = 300

        val fadeIn = Fade(Fade.IN)
        fadeIn.duration = 150

        val fadeOut = Fade(Fade.OUT)
        fadeOut.duration = 200

        transitionSet.addTransition(changeBounds)
        transitionSet.addTransition(fadeIn)
        transitionSet.addTransition(fadeOut)

        return transitionSet
    }

    private fun formatDateString(oldDateString: String): String {
        // Parse the old date string to an Instant
        val instant = Instant.parse(oldDateString)

        // Convert the Instant to a ZonedDateTime in the system's default time zone
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())

        // Define the new date format
        val newFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")

        // Format the ZonedDateTime to the new format
        return zonedDateTime.format(newFormatter)
    }

}

class OrdersDiffUtil : DiffUtil.ItemCallback<Order>() {

    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        // Check if items represent the same address by comparing their unique IDs
        return oldItem.orderNumber == newItem.orderNumber
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        // Check if the contents of the items are the same
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Order, newItem: Order): Any? {
        // You can return a payload to specify the changes between the old and new items
        return super.getChangePayload(oldItem, newItem)
    }
}
