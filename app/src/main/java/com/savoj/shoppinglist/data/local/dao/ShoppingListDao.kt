package com.savoj.shoppinglist.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.savoj.shoppinglist.data.local.entity.ShoppingListEntity
import com.savoj.shoppinglist.data.local.entity.ShoppingListWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Query("""
        SELECT 
            l.*, 
            COUNT(i.id) AS totalItems, 
            SUM(CASE WHEN i.is_completed = 1 THEN 1 ELSE 0 END) AS completedItems
        FROM shopping_lists l
        LEFT JOIN shopping_items i ON l.id = i.list_id
        WHERE l.is_archived = 0
        GROUP BY l.id
        ORDER BY l.created_at DESC
    """)
    fun getAllListsWithStats(): Flow<List<ShoppingListWithStats>>

    @Query("SELECT * FROM shopping_lists WHERE id = :id LIMIT 1")
    fun getListById(id: Long): Flow<ShoppingListEntity?>

    @Query("SELECT * FROM shopping_lists WHERE share_code = :shareCode LIMIT 1")
    suspend fun getListByShareCode(shareCode: String): ShoppingListEntity?

    @Query("UPDATE shopping_lists SET is_shared = :isShared, share_code = :shareCode WHERE id = :listId")
    suspend fun updateSharingStatus(listId: Long, isShared: Boolean, shareCode: String?): Int

    @Query("UPDATE shopping_lists SET is_locked = :isLocked WHERE id = :listId")
    suspend fun updateLockedStatus(listId: Long, isLocked: Boolean): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ShoppingListEntity): Long

    @Update
    suspend fun updateList(list: ShoppingListEntity): Int

    @Delete
    suspend fun deleteList(list: ShoppingListEntity): Int

    @Query("DELETE FROM shopping_lists WHERE id = :id")
    suspend fun deleteListById(id: Long): Int
}
