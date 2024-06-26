package com.example.ryady.model.extensions

import com.example.CustomerAddressQuery
import com.example.OrdersByCustomerTokenQuery
import com.example.ShopifyBrandsByIdQuery
import com.example.ShopifyBrandsQuery
import com.example.ShopifyProductByCategoryTypeQuery
import com.example.ShopifyProductsQuery
import com.example.ryady.Images
import com.example.ryady.model.Address
import com.example.ryady.model.Brand
import com.example.ryady.model.Item
import com.example.ryady.model.Order
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
        product.tags = edge.node.tags
        productList.add(product)
    }
    return productList
}

fun CustomerAddressQuery.Addresses.toAddressList(): ArrayList<Address> {
    val addressList: ArrayList<Address> = ArrayList()
    this.edges.forEach { address ->
        addressList.add(
            Address(
                id = address.node.id,
                firstName = address.node.firstName ?: "",
                lastName = address.node.lastName ?: "",
                country = address.node.country ?: "",
                city = address.node.city ?: "",
                zip = address.node.zip ?: "",
                address = address.node.address1 ?: "",
                phone = address.node.phone ?: "",
            )
        )
    }
    return addressList
}

fun OrdersByCustomerTokenQuery.Orders.toOrderList(): ArrayList<Order> {
    val orderList: ArrayList<Order> = ArrayList()
    this.edges.forEach { it ->
        val address = Address()
        val items = ArrayList<Item>()
        val order = Order()
        order.orderNumber = it.node.id
        order.orderName = it.node.name
        order.totalPrice = it.node.totalPrice.amount as String
        order.currencyCode = it.node.totalPrice.currencyCode.name
        order.processedAt = it.node.processedAt as String
        address.firstName = it.node.billingAddress?.firstName as String
        address.lastName = it.node.billingAddress?.lastName ?: ""
        address.address = it.node.billingAddress?.address1 ?: "N/A"
        address.city = it.node.billingAddress?.city ?: "N/A"
        address.phone = it.node.billingAddress?.phone ?: "N/A"
        address.email = it.node.email as String

        it.node.lineItems.edges.forEach { p ->
            val item = Item()
            item.productName = p.node.title
            item.quantity = p.node.quantity
            item.price = p.node.originalTotalPrice.amount as String
            item.currencyCode = p.node.originalTotalPrice.currencyCode.name
            item.vendor = p.node.variant?.product?.vendor ?: "N/A"
            item.thumbnailUrl = p.node.variant?.image?.url as String
            items.add(item)
        }
        order.address = address
        order.items = items
        orderList.add(order)
    }

    return orderList
}