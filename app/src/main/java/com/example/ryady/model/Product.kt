package com.example.ryady.model

import com.example.ryady.Images
import com.example.ryady.Variant


data class Product(
    var id: String = "",
    var title: String = "",
    var bodyHtml: String = "",
    var vendor: String = "",
    var productType: String = "",
    var availableForSale: Boolean = false,
    var maxPrice: String = "",
    var minPrice: String = "",
    var currency: String = "",
    var createdAt: String = "",
    var handle: String = "",
    var updatedAt: String = "",
    var publishedAt: String = "",
    var templateSuffix: Any? = null,
    var publishedScope: String = "",
    var tags: List<String> = emptyList(),
    var status: String = "",
    var adminGraphqlApiId: String = "",
    var variants: List<Variant> = emptyList(),
    var images: List<Images> = emptyList(),
    var vendorImageUrl: String = "",
    var imageUrl: String = "",
    var priceCode: String = "$"
)
