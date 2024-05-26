package com.example.ryady.model.extensions

import com.example.ShopifyProductsQuery
import com.example.ryady.Images
import com.example.ryady.model.Product

fun ShopifyProductsQuery.Products.toProductList(): ArrayList<Product> {
    val productList = ArrayList<Product>()
    this.edges.forEach { edge ->
        val product = Product()
        product.id = edge.node.id
        product.title = edge.node.title
        val images = ArrayList<Images>()
        edge.node.featuredImage?.url.let {
            images.add(Images(src = it as String))
        }
        edge.node.priceRange.maxVariantPrice.let {
            product.maxPrice = it.amount.toString()
            product.currency = it.currencyCode.toString()
        }
        edge.node.priceRange.minVariantPrice.let {
            product.minPrice = it.amount.toString()
            product.currency = it.currencyCode.toString()
        }
        product.availableForSale = edge.node.availableForSale
        product.images = images
        productList.add(product)
    }
    return productList
}
