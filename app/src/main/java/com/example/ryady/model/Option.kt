package com.example.ryady

data class Option(
    val id: Long,
    val productId: Long,
    val name: String,
    val position: Long,
    val values: List<String>,
)
