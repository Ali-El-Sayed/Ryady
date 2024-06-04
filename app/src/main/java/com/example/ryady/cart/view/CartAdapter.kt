package com.example.ryady.cart.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.RetrieveCartQuery
import com.example.ryady.R
import com.example.ryady.cart.viewModel.CartViewModel
import com.example.ryady.databinding.CartListItemBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.view.factory.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.internal.wait

const val TAG = "CartAdapter"
class CartAdapter(
    private var nodes: List<RetrieveCartQuery.Node>,
    private  val viewModel: CartViewModel,
    private  val passedScope: CoroutineScope,
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
    fun updateList(newnodes: List<RetrieveCartQuery.Node>){
        nodes=newnodes
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
                var curr = binding.textCount.text.toString().toInt()
                 curr++
                passedScope.launch {
                    viewModel.updateCartLine(
                        cartId = "gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUhaQVJHR1A1NlI2UlZIVEtHRVJCWkY3Tg?key=e785dd439005aa6e0b09a2b9dae2017e",
                        lineID = node.id,
                        quantity = curr
                    )
                }
            }

            binding.buttonDecrement.setOnClickListener {
                var curr = binding.textCount.text.toString().toInt()
                if (curr > 1) curr--
                passedScope.launch {
                    viewModel.updateCartLine(
                        cartId = "gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUhaQVJHR1A1NlI2UlZIVEtHRVJCWkY3Tg?key=e785dd439005aa6e0b09a2b9dae2017e",
                        lineID = node.id,
                        quantity = curr
                    )
                }
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
