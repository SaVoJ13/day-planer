package com.savoj.shoppinglist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id"],
            childColumns = ["list_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("list_id"),
        Index("category_id")
    ]
)
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "list_id")
    val listId: Long,
    val name: String,
    val quantity: Double? = null,
    val unit: String? = null,
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0,
    @ColumnInfo(name = "url")
    val url: String? = null,
    @ColumnInfo(name = "is_reserved")
    val isReserved: Boolean = false,
    @ColumnInfo(name = "reserved_by")
    val reservedBy: String? = null
)
