package com.savoj.shoppinglist.domain.model

data class ShoppingItem(
    val id: Long = 0,
    val listId: Long,
    val name: String,
    val quantity: Double? = null,
    val unit: String? = null,
    val categoryId: Long? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
