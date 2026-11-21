package com.savoj.shoppinglist.domain.repository

import com.savoj.shoppinglist.domain.model.Category
import com.savoj.shoppinglist.domain.model.RecentItem
import com.savoj.shoppinglist.domain.model.ShoppingItem
import com.savoj.shoppinglist.domain.model.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {
    fun getAllLists(): Flow<List<ShoppingList>>
    fun getListById(id: Long): Flow<ShoppingList?>
    suspend fun createList(name: String, colorHex: String = "#4CAF50"): Long
    suspend fun updateList(list: ShoppingList)
    suspend fun deleteList(listId: Long)
    suspend fun setListLocked(listId: Long, isLocked: Boolean)

    fun getItemsForList(listId: Long): Flow<List<ShoppingItem>>
    suspend fun addItem(listId: Long, name: String, quantity: Double? = null, unit: String? = null, categoryId: Long? = null): Long
    suspend fun addMultipleItems(listId: Long, rawNames: List<String>): List<Long>
    suspend fun updateItem(item: ShoppingItem)
    suspend fun setItemCompleted(itemId: Long, isCompleted: Boolean)
    suspend fun deleteItem(itemId: Long)
    suspend fun deleteCompletedItems(listId: Long)
    suspend fun resetAllItemsAsIncomplete(listId: Long)

    fun getCategories(): Flow<List<Category>>
    fun getRecentItems(): Flow<List<RecentItem>>
    fun searchRecentItems(query: String): Flow<List<RecentItem>>
}
