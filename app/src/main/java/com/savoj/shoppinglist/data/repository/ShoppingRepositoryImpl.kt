package com.savoj.shoppinglist.data.repository

import com.savoj.shoppinglist.data.local.AppDatabase
import com.savoj.shoppinglist.data.local.entity.CategoryEntity
import com.savoj.shoppinglist.data.local.entity.ShoppingItemEntity
import com.savoj.shoppinglist.data.local.entity.ShoppingListEntity
import com.savoj.shoppinglist.domain.model.Category
import com.savoj.shoppinglist.domain.model.RecentItem
import com.savoj.shoppinglist.domain.model.ShoppingItem
import com.savoj.shoppinglist.domain.model.ShoppingList
import com.savoj.shoppinglist.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShoppingRepositoryImpl(
    private val database: AppDatabase
) : ShoppingRepository {

    private val listDao = database.shoppingListDao()
    private val itemDao = database.shoppingItemDao()
    private val categoryDao = database.categoryDao()
    private val recentDao = database.recentItemDao()

    override fun getAllLists(): Flow<List<ShoppingList>> {
        return listDao.getAllListsWithStats().map { entities ->
            entities.map { stat ->
                ShoppingList(
                    id = stat.list.id,
                    name = stat.list.name,
                    colorHex = stat.list.colorHex,
                    createdAt = stat.list.createdAt,
                    totalItems = stat.totalItems,
                    completedItems = stat.completedItems,
                    isArchived = stat.list.isArchived,
                    isShared = stat.list.isShared,
                    shareCode = stat.list.shareCode,
                    isLocked = stat.list.isLocked
                )
            }
        }
    }

    override fun getListById(id: Long): Flow<ShoppingList?> {
        return listDao.getListById(id).map { entity ->
            entity?.let {
                ShoppingList(
                    id = it.id,
                    name = it.name,
                    colorHex = it.colorHex,
                    createdAt = it.createdAt,
                    isArchived = it.isArchived,
                    isShared = it.isShared,
                    shareCode = it.shareCode,
                    isLocked = it.isLocked
                )
            }
        }
    }

    override suspend fun createList(name: String, colorHex: String): Long {
        return listDao.insertList(
            ShoppingListEntity(
                name = name.trim(),
                colorHex = colorHex
            )
        )
    }

    override suspend fun updateList(list: ShoppingList) {
        listDao.updateList(
            ShoppingListEntity(
                id = list.id,
                name = list.name.trim(),
                colorHex = list.colorHex,
                createdAt = list.createdAt,
                isArchived = list.isArchived,
                isShared = list.isShared,
                shareCode = list.shareCode,
                isLocked = list.isLocked
            )
        )
    }

    override suspend fun deleteList(listId: Long) {
        listDao.deleteListById(listId)
    }

    override suspend fun setListLocked(listId: Long, isLocked: Boolean) {
        listDao.updateLockedStatus(listId, isLocked)
    }

    override fun getItemsForList(listId: Long): Flow<List<ShoppingItem>> {
        return itemDao.getItemsForList(listId).map { entities ->
            entities.map { entity ->
                ShoppingItem(
                    id = entity.id,
                    listId = entity.listId,
                    name = entity.name,
                    quantity = entity.quantity,
                    unit = entity.unit,
                    categoryId = entity.categoryId,
                    isCompleted = entity.isCompleted,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun addItem(
        listId: Long,
        name: String,
        quantity: Double?,
        unit: String?,
        categoryId: Long?
    ): Long {
        val cleanName = cleanItemName(name)
        if (cleanName.isEmpty()) return -1L

        val entity = ShoppingItemEntity(
            listId = listId,
            name = cleanName,
            quantity = quantity,
            unit = unit,
            categoryId = categoryId,
            isCompleted = false
        )
        val id = itemDao.insertItem(entity)
        recentDao.recordItemUsage(cleanName, categoryId)
        return id
    }

    override suspend fun addMultipleItems(listId: Long, rawNames: List<String>): List<Long> {
        val cleanItems = rawNames
            .map { cleanItemName(it) }
            .filter { it.isNotBlank() }

        if (cleanItems.isEmpty()) return emptyList()

        val entities = cleanItems.map { name ->
            ShoppingItemEntity(
                listId = listId,
                name = name,
                isCompleted = false
            )
        }
        val ids = itemDao.insertItems(entities)
        cleanItems.forEach { name ->
            recentDao.recordItemUsage(name)
        }
        return ids
    }

    override suspend fun updateItem(item: ShoppingItem) {
        itemDao.updateItem(
            ShoppingItemEntity(
                id = item.id,
                listId = item.listId,
                name = item.name.trim(),
                quantity = item.quantity,
                unit = item.unit,
                categoryId = item.categoryId,
                isCompleted = item.isCompleted,
                createdAt = item.createdAt
            )
        )
    }

    override suspend fun setItemCompleted(itemId: Long, isCompleted: Boolean) {
        // We can fetch item or update completed state
    }

    override suspend fun deleteItem(itemId: Long) {
        itemDao.deleteItemById(itemId)
    }

    override suspend fun deleteCompletedItems(listId: Long) {
        itemDao.deleteCompletedItems(listId)
    }

    override suspend fun resetAllItemsAsIncomplete(listId: Long) {
        itemDao.resetAllItemsAsIncomplete(listId)
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { list ->
            list.map { entity ->
                Category(
                    id = entity.id,
                    nameKey = entity.nameKey,
                    iconName = entity.iconName,
                    colorHex = entity.colorHex,
                    orderIndex = entity.orderIndex
                )
            }
        }
    }

    override fun getRecentItems(): Flow<List<RecentItem>> {
        return recentDao.getTopRecentItems(15).map { list ->
            list.map { entity ->
                RecentItem(
                    id = entity.id,
                    name = entity.name,
                    categoryId = entity.categoryId,
                    useCount = entity.useCount
                )
            }
        }
    }

    override fun searchRecentItems(query: String): Flow<List<RecentItem>> {
        return recentDao.searchRecentItems(query, 5).map { list ->
            list.map { entity ->
                RecentItem(
                    id = entity.id,
                    name = entity.name,
                    categoryId = entity.categoryId,
                    useCount = entity.useCount
                )
            }
        }
    }

    companion object {
        fun cleanItemName(raw: String): String {
            return raw.trim()
                .replace(Regex("^[\\s*•\\-–—]+"), "") // Remove bullets: -, *, •
                .replace(Regex("^\\d+[.)]\\s*"), "") // Remove numbering: 1., 2)
                .trim()
        }

        fun parseMultiLineText(text: String): List<String> {
            if (text.isBlank()) return emptyList()

            val normalized = text
                .replace(Regex("(?i)\\s+(і|та|й|and|or)\\s+"), ", ")
                .replace(";", ",")

            val lines = normalized.split(Regex("[\n\r,]+"))

            return lines
                .map { cleanItemName(it) }
                .filter { it.isNotBlank() }
                .distinct()
        }
    }
}
