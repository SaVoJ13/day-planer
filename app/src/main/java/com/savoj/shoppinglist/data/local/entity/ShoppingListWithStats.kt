package com.savoj.shoppinglist.data.local.entity

import androidx.room.Embedded

data class ShoppingListWithStats(
    @Embedded
    val list: ShoppingListEntity,
    val totalItems: Int = 0,
    val completedItems: Int = 0
)
