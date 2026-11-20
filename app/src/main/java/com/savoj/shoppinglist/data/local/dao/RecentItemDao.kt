package com.savoj.shoppinglist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.savoj.shoppinglist.data.local.entity.RecentItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentItemDao {
    @Query("SELECT * FROM recent_items ORDER BY use_count DESC, last_used_at DESC LIMIT :limit")
    fun getTopRecentItems(limit: Int = 10): Flow<List<RecentItemEntity>>

    @Query("SELECT * FROM recent_items WHERE name LIKE '%' || :query || '%' ORDER BY use_count DESC LIMIT :limit")
    fun searchRecentItems(query: String, limit: Int = 5): Flow<List<RecentItemEntity>>

    @Query("SELECT * FROM recent_items WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun findByName(name: String): RecentItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: RecentItemEntity): Long

    @Transaction
    suspend fun recordItemUsage(name: String, categoryId: Long? = null): Boolean {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return false
        val existing = findByName(trimmed)
        if (existing != null) {
            insertOrUpdate(
                existing.copy(
                    useCount = existing.useCount + 1,
                    lastUsedAt = System.currentTimeMillis(),
                    categoryId = categoryId ?: existing.categoryId
                )
            )
        } else {
            insertOrUpdate(
                RecentItemEntity(
                    name = trimmed,
                    categoryId = categoryId,
                    lastUsedAt = System.currentTimeMillis(),
                    useCount = 1
                )
            )
        }
        return true
    }
}
