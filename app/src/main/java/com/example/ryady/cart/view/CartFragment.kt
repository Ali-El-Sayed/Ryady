package com.example.ryady.cart.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.RetrieveCartQuery
import com.example.payment.BillingData
import com.example.payment.Item
import com.example.payment.PaymentRequest
import com.example.payment.State
import com.example.ryady.cart.viewModel.CartViewModel
import com.example.ryady.databinding.FragmentCartBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.paymob.paymob_sdk.PaymobSdk
import com.paymob.paymob_sdk.ui.PaymobSdkListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class CartFragment : Fragment(), PaymobSdkListener {

    lateinit var binding: FragmentCartBinding
    var cartId =""
    var nlist:ArrayList<RetrieveCartQuery.Node> = ArrayList()
    lateinit var buyer:RetrieveCartQuery.BuyerIdentity
     var total:Double =0.0
    var mytax:Int = 0
    var pricessummed:Int =0
    lateinit var myadapter:CartAdapter

    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[CartViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.fetchCartById("gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUhaQVJHR1A1NlI2UlZIVEtHRVJCWkY3Tg?key=e785dd439005aa6e0b09a2b9dae2017e")
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater,container,false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cartRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
         myadapter = CartAdapter(nodes = nlist, viewModel = viewModel, passedScope = lifecycleScope){
            // the onclick procedure
        }
        binding.cartRecycler.adapter = myadapter

        lifecycleScope.launch {
            viewModel.cartInfo.collectLatest{ result ->
                when(result){
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> {
                        nlist.clear()
                        result.data.lines.edges.forEach {
                            nlist.add(it.node)
                        }
                         mytax = ((result.data.cost.totalAmount.amount.toString().toDouble() - result.data.cost.checkoutChargeAmount.amount.toString().toDouble())*100).toInt()

                        buyer = result.data.buyerIdentity
                        total=result.data.cost.totalAmount.amount.toString().toDouble()
                        binding.totalPrice.text = result.data.cost.totalAmount.amount.toString()+" "+result.data.cost.totalAmount.currencyCode.toString()
                        binding.subtotalPrice.text = result.data.cost.checkoutChargeAmount.amount.toString()
                        binding.tax.text = BigDecimal(result.data.cost.totalAmount.amount.toString().toDouble() - result.data.cost.checkoutChargeAmount.amount.toString().toDouble()).setScale(2, RoundingMode.HALF_EVEN).toDouble().toString()
                        myadapter.updateList(nlist)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.updateCartItemInfo.collectLatest {result ->
                when(result){
                    is Response.Error -> {}
                    is Response.Loading -> {}
                    is Response.Success -> {
                        viewModel.fetchCartById("gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUhaQVJHR1A1NlI2UlZIVEtHRVJCWkY3Tg?key=e785dd439005aa6e0b09a2b9dae2017e")
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.order.collectLatest { result ->
                when (result) {

                    is State.Failure -> {}
                    State.Loading -> {}
                    is State.Success -> {
                        Log.d("Secret", result.data.clientSecret ?: "no secret")
                        val paymobsdk = PaymobSdk.Builder(
                            context = requireContext(),
                            clientSecret = result.data.clientSecret ?: "",  //  Place Client Secret here
                            publicKey = "egy_pk_test_FgzmlcKNjL1wftgERMqnpEHTyk09tLCY", // Place Public Key here
                            paymobSdkListener = this@CartFragment,
                        ).build()

                        paymobsdk.start()
                    }
                }
            }
        }

        binding.button.setOnClickListener {
            var items = ArrayList<Item>()
            nlist.forEach {
                pricessummed+=((it.merchandise.onProductVariant?.price?.amount.toString().toDouble()*100).toInt()*it.quantity)
                Log.d("Prices", ((it.merchandise.onProductVariant?.price?.amount.toString().toDouble()*100).toInt()*it.quantity).toString())
                Log.d("Pricessummed", pricessummed.toString())
                (it.merchandise.onProductVariant?.price?.amount.toString().toDouble()*100).toInt()
                var item =  Item(name = it.merchandise?.onProductVariant?.title ?: "no title", amount = (it.merchandise.onProductVariant?.price?.amount.toString().toDouble()*100).toInt(), description = it.merchandise.onProductVariant?.barcode ?: "barcode missing", quantity = it.quantity)
                items.add(item)
            }
            val tax = Item(name = "Tax", amount = mytax , description = "tax", quantity = 1)
            items.add(tax)
            pricessummed+= mytax
            Log.d("Prices", mytax.toString())
            Log.d("Pricessummed", pricessummed.toString())
            val billingData = BillingData(first_name = buyer.customer?.firstName ?: "no name", last_name = buyer.customer?.lastName ?: "no name", email = buyer.email ?: "no email" , phone_number = buyer.customer?.phone ?: "no phone number" , street = "null" , building = "null" , city = "null" , country = "null")

            var methods = ArrayList<Int>()
            methods.add(4589069)
            Log.d("Pricessummed", pricessummed.toString())
            Log.d("total", (total*100).toInt().toString())
            val request = PaymentRequest(amount =  (total*100).toInt(), currency = nlist.first().cost.totalAmount.currencyCode.name , payment_methods = methods, items = items , billingData)
            viewModel.createPayment(request)
        }
    }

    override fun onFailure() {
        TODO("Not yet implemented")
    }

    override fun onPending() {
        TODO("Not yet implemented")
    }

    override fun onSuccess() {
        TODO("Not yet implemented")
    }

}
