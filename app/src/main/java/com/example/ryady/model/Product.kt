package com.example.ryady.model

import com.example.ryady.Image
import com.example.ryady.Images
import com.example.ryady.Option
import com.example.ryady.Variant

data class Product(
    var id: String = "",
    var title: String = "",
    var bodyHtml: String = "",
    var vendor: String = "",
    var productType: String = "",
    var createdAt: String = "",
    var handle: String = "",
    var updatedAt: String = "",
    var publishedAt: String = "",
    var templateSuffix: Any? = null,
    var publishedScope: String = "",
    var tags: String = "",
    var status: String = "",
    var adminGraphqlApiId: String = "",
    var variants: List<Variant> = emptyList(),
    var options: List<Option> = emptyList(),
    var images: List<Images> = emptyList(),
    var image: Image = Image(),
)
