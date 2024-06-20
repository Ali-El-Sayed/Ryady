package com.example.ryady.view.screens.cart.view

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.RetrieveCartQuery
import com.example.ryady.R
import com.example.ryady.databinding.CartListItemBinding
import com.example.ryady.view.screens.cart.viewModel.CartViewModel
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

const val TAG = "CartAdapter"

class CartAdapter(
    private var nodes: List<RetrieveCartQuery.Node>,
    private val viewModel: CartViewModel,
    private val passedScope: CoroutineScope,
    private val context: Context,
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

    fun updateList(newnodes: List<RetrieveCartQuery.Node>) {
        nodes = newnodes
        notifyDataSetChanged()
        Log.d(TAG, "Updated data in adapter. New node count: ${nodes.size}")

    }

    inner class ViewHolder(private val binding: CartListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(node: RetrieveCartQuery.Node) {
            binding.favView.setOnLongClickListener {
                binding.checkb.visibility = View.VISIBLE
                true
            }

            binding.buttonIncrement.setOnClickListener {
                binding.textCount.visibility = View.INVISIBLE
                binding.animationView.visibility = View.VISIBLE
                var curr = binding.textCount.text.toString().toInt()
                curr++
                passedScope.launch {
                    viewModel.updateCartLine(
                        viewModel.cartId,
                        lineID = node.id,
                        quantity = curr
                    )
                }
            }

            binding.buttonDecrement.setOnClickListener {

                var curr = binding.textCount.text.toString().toInt()
                if (curr > 1) {
                    curr--
                    binding.textCount.visibility = View.INVISIBLE
                    binding.animationView.visibility = View.VISIBLE
                    passedScope.launch {
                        viewModel.updateCartLine(
                            viewModel.cartId,
                            lineID = node.id,
                            quantity = curr
                        )
                    }
                } else {
                    passedScope.launch {
                        viewModel.deleteCartLine(
                            viewModel.cartId,
                            lineID = node.id
                        )
                    }
                }

            }
            binding.deleteLayout.setOnClickListener {
                passedScope.launch {
                    viewModel.deleteCartLine(
                        viewModel.cartId,
                        lineID = node.id
                    )
                }
            }
            fun Double.roundTo2DecimalPlaces(): Double {
                return BigDecimal(this).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            }

            fun deleteLambda() {
                val modifiedText = "%icon%" // you can use resource string here
                val span = SpannableString(modifiedText)
                val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_delete, null)
                drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                val image = drawable?.let { ImageSpan(it, ImageSpan.ALIGN_BOTTOM) }
                val startIndex = modifiedText.indexOf("%icon%")

                //Replace %icon% with drawable
                span.setSpan(image, startIndex, startIndex + 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                binding.buttonDecrement.text = span
            }

            binding.cartProductTitle.text = node.merchandise.onProductVariant?.product?.title
            binding.cartProductBrand.text = node.merchandise.onProductVariant?.product?.vendor
            if (node.quantity == 1) {
                deleteLambda()
            } else {
                binding.buttonDecrement.text = "-"
            }
            if (node.merchandise.onProductVariant?.quantityAvailable == node.quantity) {
                binding.buttonIncrement.isClickable = false
                binding.buttonIncrement.alpha = 0.5f
            } else {
                binding.buttonIncrement.isClickable = true
                binding.buttonIncrement.alpha = 1f
            }
            binding.textCount.text = node.quantity.toString()
            binding.textCount.visibility = View.VISIBLE
            binding.animationView.visibility = View.GONE
            val total = node.cost.totalAmount.amount.toString().toDouble()
            val totalExchanged = total / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                TheExchangeRate.chosenCurrency.first
            )!!)

            val priceText = "${totalExchanged.roundTo2DecimalPlaces().toString()} ${TheExchangeRate.chosenCurrency.first}"
            binding.price.text = priceText
            binding.imageView.load(node.merchandise.onProductVariant?.image?.src) {
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.loading_placeholder)
            }

            itemView.setOnClickListener { onMerchandiseClick(node.id) }
        }
    }
}
