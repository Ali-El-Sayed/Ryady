package com.example.ryady.view.screens.productsByBrand.viewmodel

import android.util.Range
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Currency
import com.example.ryady.model.Product
import com.example.ryady.network.model.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ProductsViewmodel"

class ProductsViewmodel(
    private val remoteDataSource: IRemoteDataSource,
    private val currency: Currency,
    private val chosenCurrency: Pair<String, String>
) : ViewModel() {
    private var products: MutableStateFlow<Response<List<Product>>> = MutableStateFlow(Response.Loading())
    val productList = products.asStateFlow()
    private var productsByCategory: MutableStateFlow<Response<List<Product>>> = MutableStateFlow(Response.Loading())
    val productsByCategoryList = productsByCategory.asStateFlow()
    var categoryType: CategoryType = CategoryType.ALL
    var humanType: HumanType = HumanType.ALL
    var priceRange: Range<Double> = Range.create(0.0, 10000000000.0)

    init {
        viewModelScope.launch {
            getProductsByCategory()
        }
    }

    suspend fun getProductsByBrandId(id: String) {
        val response = remoteDataSource.fetchProductsByBrandId<Response<ArrayList<Product>>>(id)
        when (response) {
            is Response.Loading -> products.value = Response.Loading()
            is Response.Success -> products.value = Response.Success(response.data as ArrayList<Product>)
            is Response.Error -> products.value = Response.Error(response.message)
        }
    }

    suspend fun getProductsByCategory() {
        productsByCategory.emit(Response.Loading())
        val response = remoteDataSource.fetchProductsByCategory<Response<List<Product>>>(categoryType.category)
        when (response) {
            is Response.Loading -> productsByCategory.emit(Response.Loading())
            is Response.Success -> {
                val filteredProducts = (response.data as ArrayList<Product>).filter {
                    val maxPrice = it.maxPrice.toDouble()

                    val maxPriceExchanged = maxPrice / (currency.rates?.get("EGP")!!) * (currency.rates?.get(
                        chosenCurrency.first
                    )!!)
                    val minPrice = it.minPrice.toDouble()
                    val minPriceExchanged = minPrice / (currency.rates?.get("EGP")!!) * (currency.rates?.get(
                        chosenCurrency.first
                    )!!)
                    it.tags.joinToString().contains(humanType.type) && priceRange.contains(
                        Range.create(
                            minPriceExchanged, maxPriceExchanged
                        )
                    )
                }
                productsByCategory.emit(Response.Success(filteredProducts))
            }

            is Response.Error -> products.emit(Response.Error(response.message))
        }
    }
}

enum class CategoryType(val category: String) {
    ACCESSORIES("product_type:ACCESSORIES"), SHOES("product_type:SHOES"), T_SHIRTS("product_type:T-SHIRTS"), ALL("")
}

enum class HumanType(val type: String) {
    MEN("men"), WOMEN("women"), KIDS("kids"), ALL("")
}