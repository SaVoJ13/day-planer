package com.savoj.shoppinglist.domain.model

data class ShoppingList(
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#4CAF50",
    val createdAt: Long = System.currentTimeMillis(),
    val totalItems: Int = 0,
    val completedItems: Int = 0,
    val isArchived: Boolean = false,
    val isShared: Boolean = false,
    val shareCode: String? = null,
    val isLocked: Boolean = false,
    val isWishlist: Boolean = false,
    val occasion: String? = null
)
