package com.savoj.shoppinglist.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.savoj.shoppinglist.data.local.entity.ShoppingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shopping_items WHERE list_id = :listId ORDER BY is_completed ASC, order_index ASC, id ASC")
    fun getItemsForList(listId: Long): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItemEntity): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ShoppingItemEntity>): List<Long>

    @Update
    suspend fun updateItem(item: ShoppingItemEntity): Int

    @Delete
    suspend fun deleteItem(item: ShoppingItemEntity): Int

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteItemById(id: Long): Int

    @Query("DELETE FROM shopping_items WHERE list_id = :listId AND is_completed = 1")
    suspend fun deleteCompletedItems(listId: Long): Int

    @Query("UPDATE shopping_items SET is_completed = 0 WHERE list_id = :listId")
    suspend fun resetAllItemsAsIncomplete(listId: Long): Int

    @Query("UPDATE shopping_items SET is_reserved = :isReserved, reserved_by = :reservedBy WHERE id = :itemId")
    suspend fun updateReservationStatus(itemId: Long, isReserved: Boolean, reservedBy: String?): Int

    @Query("UPDATE shopping_items SET url = :url WHERE id = :itemId")
    suspend fun updateItemUrl(itemId: Long, url: String?): Int
}
