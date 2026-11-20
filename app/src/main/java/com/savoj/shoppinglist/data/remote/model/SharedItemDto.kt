package com.savoj.shoppinglist.data.remote.model

import com.google.firebase.Timestamp

data class SharedItemDto(
    val id: String = "",
    val name: String = "",
    val quantity: String = "",
    val unit: String = "",
    val categoryName: String = "",
    val isBought: Boolean = false,
    val addedBy: String = "",
    val updatedAt: Timestamp? = null
)
