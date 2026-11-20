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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ShoppingListEntity): Long

    @Update
    suspend fun updateList(list: ShoppingListEntity): Int

    @Delete
    suspend fun deleteList(list: ShoppingListEntity): Int

    @Query("DELETE FROM shopping_lists WHERE id = :id")
    suspend fun deleteListById(id: Long): Int
}
