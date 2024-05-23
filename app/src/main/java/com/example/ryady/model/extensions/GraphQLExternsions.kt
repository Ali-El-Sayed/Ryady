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
        edge.node.images.edges.forEach {
            images.add(Images(src = it.node.url as String))
            print(it.node.url)
        }
        product.images = images
        productList.add(product)
    }
    return productList
}
