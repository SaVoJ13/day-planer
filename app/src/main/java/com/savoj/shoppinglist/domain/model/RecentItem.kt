package com.savoj.shoppinglist.domain.model

data class RecentItem(
    val id: Long = 0,
    val name: String,
    val categoryId: Long? = null,
    val useCount: Int = 1
)
