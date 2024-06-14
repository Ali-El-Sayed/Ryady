package com.example.ryady.view.screens.productsByBrand.viewmodel

import android.util.Range
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ryady.datasource.remote.IRemoteDataSource
import com.example.ryady.model.Product
import com.example.ryady.network.model.Response
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ProductsViewmodel"

class ProductsViewmodel(private val remoteDataSource: IRemoteDataSource) : ViewModel() {
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
        productsByCategory.value = Response.Loading()
        val response = remoteDataSource.fetchProductsByCategory<Response<List<Product>>>(categoryType.category)
        when (response) {
            is Response.Loading -> productsByCategory.value = Response.Loading()
            is Response.Success -> {
                val filteredProducts = (response.data as ArrayList<Product>).filter {
                    val maxPrice = it.maxPrice.toDouble()

                    val maxPriceExchanged =
                        maxPrice / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                            TheExchangeRate.choosedCurrency.first
                        )!!)
                    val minPrice = it.minPrice.toDouble()
                    val minPriceExchanged =
                        minPrice / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
                            TheExchangeRate.choosedCurrency.first
                        )!!)
                    it.tags.joinToString().contains(humanType.type) && priceRange.contains(
                        Range.create(
                            minPriceExchanged,
                            maxPriceExchanged
                        )
                    )
                }
                productsByCategory.value = Response.Success(filteredProducts)
            }

            is Response.Error -> products.value = Response.Error(response.message)
        }
    }
}

enum class CategoryType(val category: String) {
    ACCESSORIES("product_type:ACCESSORIES"), SHOES("product_type:SHOES"), T_SHIRTS("product_type:T-SHIRTS"), ALL("")
}

enum class HumanType(val type: String) {
    MEN("men"), WOMEN("women"), KIDS("kids"), ALL("")
}