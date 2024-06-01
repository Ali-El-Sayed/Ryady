package com.example.ryady.model.extensions

import com.example.ShopifyBrandsByIdQuery
import com.example.ShopifyBrandsQuery
import com.example.ShopifyProductByCategoryTypeQuery
import com.example.ShopifyProductsQuery
import com.example.ryady.Images
import com.example.ryady.model.Brand
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
        product.vendor = edge.node.vendor
        product.availableForSale = edge.node.availableForSale
        product.images = images
        productList.add(product)
    }
    return productList
}

fun ShopifyBrandsByIdQuery.Products.toProductList(): ArrayList<Product> {
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
        product.vendor = edge.node.vendor
        product.availableForSale = edge.node.availableForSale
        product.images = images
        productList.add(product)
    }
    return productList
}

fun ShopifyBrandsQuery.Collections.toBrandsList(): ArrayList<Brand> {
    val brandsList = ArrayList<Brand>()
    this.edges.forEach { edge ->
        // skip first one
        if (edge.node.title != "Home page") {
            val brand = Brand()
            brand.id = edge.node.id
            brand.title = edge.node.title
            edge.node.image?.let {
                brand.imageUrl = it.url as String
            }
            brandsList.add(brand)
        }
    }
    return brandsList
}

fun ShopifyProductByCategoryTypeQuery.Products.toProductList(): ArrayList<Product> {
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
        product.vendor = edge.node.vendor
        product.images = images
        productList.add(product)
    }
    return productList
}