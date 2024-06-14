package com.example.ryady.view.screens.settings.address.view.adapter

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
import com.example.ryady.databinding.AddressCardBinding
import com.example.ryady.model.Address


class AddressListAdapter : ListAdapter<Address, AddressListAdapter.ViewHolder>(AddressDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AddressCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = getItem(position)
        holder.bind(address)
    }


    inner class ViewHolder(private val binding: AddressCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address) {
            binding.arrowButton.setOnClickListener {
                if (binding.hiddenView.visibility == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(binding.baseCardview, getCustomTransition())
                    binding.hiddenView.visibility = View.GONE
                    binding.arrowButton.setImageResource(R.drawable.ic_arrow_down)
                    binding.addressText.clearFocus()
                } else {
                    TransitionManager.beginDelayedTransition(binding.baseCardview, getCustomTransition())
                    binding.hiddenView.visibility = View.VISIBLE
                    binding.arrowButton.setImageResource(R.drawable.ic_arrow_up)
                    binding.addressText.requestFocus()
                }
            }
            address.let {
                if (it.address.isNotEmpty()) binding.addressText.text = it.address
                if (it.firstName.isNotEmpty()) binding.firstNameText.text = it.firstName
                if (it.lastName.isNotEmpty()) binding.lastNameText.text = it.lastName
                if (it.phone.isNotEmpty()) binding.phoneText.text = it.phone
                if (it.zip.isNotEmpty()) binding.zipText.text = it.zip
                if (it.city.isNotEmpty()) binding.cityText.text = it.city
                if (it.country.isNotEmpty()) binding.countryText.text = it.country
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
    }

}

class AddressDiffUtil : DiffUtil.ItemCallback<Address>() {

    override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
        // Check if items represent the same address by comparing their unique IDs
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
        // Check if the contents of the items are the same
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Address, newItem: Address): Any? {
        // You can return a payload to specify the changes between the old and new items
        return super.getChangePayload(oldItem, newItem)
    }
}
