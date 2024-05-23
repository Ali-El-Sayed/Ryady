package com.example.ryady

data class Image(
    val id: Long = 0,
    val alt: Any? = null, // Already nullable
    val position: Long = 0,
    val productId: Long = 0,
    val createdAt: String = "",
    val updatedAt: String = "",
    val adminGraphqlApiId: String = "",
    val width: Long = 0,
    val height: Long = 0,
    val src: String = "",
)
