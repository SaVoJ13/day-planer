package com.savoj.shoppinglist.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.savoj.shoppinglist.data.local.dao.ShoppingItemDao
import com.savoj.shoppinglist.data.local.dao.ShoppingListDao
import com.savoj.shoppinglist.data.local.entity.ShoppingItemEntity
import com.savoj.shoppinglist.data.local.entity.ShoppingListEntity
import com.savoj.shoppinglist.data.remote.model.SharedItemDto
import com.savoj.shoppinglist.data.remote.model.SharedListDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GroupShoppingRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val listDao: ShoppingListDao,
    private val itemDao: ShoppingItemDao
) {
    suspend fun ensureAuthenticated(): String {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return currentUser.uid
        }
        val authResult = auth.signInAnonymously().await()
        return authResult.user?.uid ?: throw IllegalStateException("Firebase Anonymous Auth returned null user")
    }

    suspend fun createSharedList(
        localListId: Long,
        name: String,
        items: List<ShoppingItemEntity>
    ): Result<String> {
        val shareCode = ShareCodeGenerator.generateCode()

        // Update local database immediately so code is persistent and available instantly
        listDao.updateSharingStatus(localListId, true, shareCode)

        // Attempt cloud sync in background/safe execution
        runCatching {
            val uid = ensureAuthenticated()
            val listData = hashMapOf(
                "name" to name,
                "creatorId" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            val docRef = firestore.collection("shared_lists").document(shareCode)
            docRef.set(listData).await()

            // Batch upload existing items
            if (items.isNotEmpty()) {
                val batch = firestore.batch()
                for (item in items) {
                    val itemDoc = docRef.collection("items").document(item.id.toString())
                    val itemData = hashMapOf(
                        "id" to item.id.toString(),
                        "name" to item.name,
                        "quantity" to (item.quantity?.toString() ?: ""),
                        "unit" to (item.unit ?: ""),
                        "categoryName" to "",
                        "isBought" to item.isCompleted,
                        "addedBy" to uid,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    batch.set(itemDoc, itemData)
                }
                batch.commit().await()
            }
        }

        return Result.success(shareCode)
    }

    suspend fun joinSharedList(shareCodeInput: String): Result<Long> = runCatching {
        val code = ShareCodeGenerator.normalizeCode(shareCodeInput)
        if (!ShareCodeGenerator.isValidCode(code)) {
            throw IllegalArgumentException("Invalid share code format: must be 6 characters")
        }
        ensureAuthenticated()

        val docSnapshot = firestore.collection("shared_lists").document(code).get().await()
        if (!docSnapshot.exists()) {
            throw NoSuchElementException("Shared list not found for code: $code")
        }

        val remoteName = docSnapshot.getString("name") ?: "Shared List"
        val existingLocal = listDao.getListByShareCode(code)
        val localListId: Long = if (existingLocal != null) {
            existingLocal.id
        } else {
            val newList = ShoppingListEntity(
                name = remoteName,
                isShared = true,
                shareCode = code
            )
            listDao.insertList(newList)
        }

        // Fetch remote items and sync into local DB
        val itemsSnapshot = firestore.collection("shared_lists").document(code)
            .collection("items").get().await()

        for (doc in itemsSnapshot.documents) {
            val itemName = doc.getString("name") ?: continue
            val itemQuantity = doc.getString("quantity") ?: ""
            val itemUnit = doc.getString("unit") ?: ""
            val itemCategory = doc.getString("categoryName")
            val isBought = doc.getBoolean("isBought") ?: false

            val localItem = ShoppingItemEntity(
                listId = localListId,
                name = itemName,
                quantity = itemQuantity.toDoubleOrNull(),
                unit = itemUnit.ifBlank { null },
                categoryId = null,
                isCompleted = isBought
            )
            itemDao.insertItem(localItem)
        }

        localListId
    }

    fun observeSharedItems(shareCode: String): Flow<List<SharedItemDto>> = callbackFlow {
        val code = ShareCodeGenerator.normalizeCode(shareCode)
        val listenerRegistration = firestore.collection("shared_lists")
            .document(code)
            .collection("items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        val name = doc.getString("name") ?: return@mapNotNull null
                        SharedItemDto(
                            id = doc.id,
                            name = name,
                            quantity = doc.getString("quantity") ?: "",
                            unit = doc.getString("unit") ?: "",
                            categoryName = doc.getString("categoryName") ?: "",
                            isBought = doc.getBoolean("isBought") ?: false,
                            addedBy = doc.getString("addedBy") ?: "",
                            updatedAt = doc.getTimestamp("updatedAt")
                        )
                    }
                    trySend(items)
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    suspend fun addSharedItem(
        shareCode: String,
        name: String,
        quantity: String,
        unit: String,
        categoryName: String
    ): Result<String> = runCatching {
        val uid = ensureAuthenticated()
        val code = ShareCodeGenerator.normalizeCode(shareCode)
        val itemDoc = firestore.collection("shared_lists").document(code)
            .collection("items").document()

        val itemData = hashMapOf(
            "id" to itemDoc.id,
            "name" to name.trim(),
            "quantity" to quantity.trim(),
            "unit" to unit.trim(),
            "categoryName" to categoryName.trim(),
            "isBought" to false,
            "addedBy" to uid,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        itemDoc.set(itemData).await()
        firestore.collection("shared_lists").document(code)
            .update("updatedAt", FieldValue.serverTimestamp())

        itemDoc.id
    }

    suspend fun toggleSharedItem(
        shareCode: String,
        itemId: String,
        isBought: Boolean
    ): Result<Unit> = runCatching {
        val code = ShareCodeGenerator.normalizeCode(shareCode)
        firestore.collection("shared_lists").document(code)
            .collection("items").document(itemId)
            .update(
                mapOf(
                    "isBought" to isBought,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
    }

    suspend fun deleteSharedItem(
        shareCode: String,
        itemId: String
    ): Result<Unit> = runCatching {
        val code = ShareCodeGenerator.normalizeCode(shareCode)
        firestore.collection("shared_lists").document(code)
            .collection("items").document(itemId)
            .delete().await()
    }
}
