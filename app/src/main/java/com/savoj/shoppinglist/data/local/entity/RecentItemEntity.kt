package com.savoj.shoppinglist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent_items",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class RecentItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "name", collate = ColumnInfo.NOCASE)
    val name: String,
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "use_count")
    val useCount: Int = 1
)
