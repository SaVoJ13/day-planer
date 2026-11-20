package com.savoj.shoppinglist.domain.model

data class Category(
    val id: Long,
    val nameKey: String,
    val iconName: String,
    val colorHex: String,
    val orderIndex: Int
)
