package com.savoj.shoppinglist.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savoj.shoppinglist.data.local.entity.ShoppingItemEntity
import com.savoj.shoppinglist.data.remote.GroupShoppingRepository
import com.savoj.shoppinglist.data.repository.ShoppingRepositoryImpl
import com.savoj.shoppinglist.domain.model.Category
import com.savoj.shoppinglist.domain.model.RecentItem
import com.savoj.shoppinglist.domain.model.ShoppingItem
import com.savoj.shoppinglist.domain.model.ShoppingList
import com.savoj.shoppinglist.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ListDetailsUiState(
    val currentList: ShoppingList? = null,
    val items: List<ShoppingItem> = emptyList(),
    val categories: List<Category> = emptyList(),
    val recentItems: List<RecentItem> = emptyList(),
    val showPasteDialog: Boolean = false,
    val parsedPasteItems: List<String> = emptyList(),
    val showRecentSheet: Boolean = false,
    val lastDeletedItem: ShoppingItem? = null,
    val showCompletedSection: Boolean = true,
    val showShareDialog: Boolean = false,
    val isSharingLoading: Boolean = false,
    val shareError: String? = null
)

class ListDetailsViewModel(
    private val listId: Long,
    private val repository: ShoppingRepository,
    private val groupRepository: GroupShoppingRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListDetailsUiState())
    val uiState: StateFlow<ListDetailsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getListById(listId).collect { list ->
                _uiState.value = _uiState.value.copy(currentList = list)
                // If list is shared, observe remote updates
                if (list?.isShared == true && list.shareCode != null && groupRepository != null) {
                    observeRemoteList(list.shareCode)
                }
            }
        }
        viewModelScope.launch {
            repository.getItemsForList(listId).collect { items ->
                _uiState.value = _uiState.value.copy(items = items)
            }
        }
        viewModelScope.launch {
            repository.getCategories().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
        viewModelScope.launch {
            repository.getRecentItems().collect { recents ->
                _uiState.value = _uiState.value.copy(recentItems = recents)
            }
        }
    }

    private fun observeRemoteList(shareCode: String) {
        if (groupRepository == null) return
        viewModelScope.launch {
            groupRepository.observeSharedItems(shareCode).collect { remoteItems ->
                // Remote items updated in real time - can be refreshed
            }
        }
    }

    fun showShareDialog() {
        val list = _uiState.value.currentList ?: return
        if (list.isShared && list.shareCode != null) {
            _uiState.value = _uiState.value.copy(showShareDialog = true)
        } else {
            createAndShareList()
        }
    }

    fun hideShareDialog() {
        _uiState.value = _uiState.value.copy(showShareDialog = false, shareError = null)
    }

    fun createAndShareList() {
        if (groupRepository == null) return
        val list = _uiState.value.currentList ?: return
        val currentItems = _uiState.value.items
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSharingLoading = true, shareError = null)
            val entityItems = currentItems.map { item ->
                ShoppingItemEntity(
                    id = item.id,
                    listId = listId,
                    name = item.name,
                    quantity = item.quantity,
                    unit = item.unit,
                    categoryId = item.categoryId,
                    isCompleted = item.isCompleted
                )
            }
            val result = groupRepository.createSharedList(listId, list.name, entityItems)
            _uiState.value = _uiState.value.copy(isSharingLoading = false)
            result.onSuccess { code ->
                _uiState.value = _uiState.value.copy(
                    showShareDialog = true,
                    currentList = _uiState.value.currentList?.copy(isShared = true, shareCode = code)
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    shareError = err.message ?: "Помилка створення спільного списку"
                )
            }
        }
    }

    fun addItem(name: String, quantity: Double? = null, unit: String? = null, categoryId: Long? = null) {
        val cleanName = ShoppingRepositoryImpl.cleanItemName(name)
        if (cleanName.isBlank()) return
        viewModelScope.launch {
            repository.addItem(listId, cleanName, quantity, unit, categoryId)
            val list = _uiState.value.currentList
            if (list?.isShared == true && list.shareCode != null && groupRepository != null) {
                groupRepository.addSharedItem(
                    shareCode = list.shareCode,
                    name = cleanName,
                    quantity = quantity?.toString() ?: "",
                    unit = unit ?: "",
                    categoryName = ""
                )
            }
        }
    }

    fun toggleItemCompleted(item: ShoppingItem) {
        viewModelScope.launch {
            val updated = item.copy(isCompleted = !item.isCompleted)
            repository.updateItem(updated)
            val list = _uiState.value.currentList
            if (list?.isShared == true && list.shareCode != null && groupRepository != null) {
                groupRepository.toggleSharedItem(list.shareCode, item.id.toString(), updated.isCompleted)
            }
        }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(lastDeletedItem = item)
            repository.deleteItem(item.id)
            val list = _uiState.value.currentList
            if (list?.isShared == true && list.shareCode != null && groupRepository != null) {
                groupRepository.deleteSharedItem(list.shareCode, item.id.toString())
            }
        }
    }

    fun undoDelete() {
        val itemToRestore = _uiState.value.lastDeletedItem ?: return
        viewModelScope.launch {
            repository.addItem(
                listId = itemToRestore.listId,
                name = itemToRestore.name,
                quantity = itemToRestore.quantity,
                unit = itemToRestore.unit,
                categoryId = itemToRestore.categoryId
            )
            _uiState.value = _uiState.value.copy(lastDeletedItem = null)
        }
    }

    fun deleteCompletedItems() {
        viewModelScope.launch {
            repository.deleteCompletedItems(listId)
        }
    }

    fun resetAllItems() {
        viewModelScope.launch {
            repository.resetAllItemsAsIncomplete(listId)
        }
    }

    fun onClipboardPasted(text: String) {
        val parsed = ShoppingRepositoryImpl.parseMultiLineText(text)
        if (parsed.size > 1) {
            _uiState.value = _uiState.value.copy(
                showPasteDialog = true,
                parsedPasteItems = parsed
            )
        } else if (parsed.size == 1) {
            addItem(parsed.first())
        }
    }

    fun confirmPaste(selectedItems: List<String>) {
        if (selectedItems.isEmpty()) return
        viewModelScope.launch {
            repository.addMultipleItems(listId, selectedItems)
            dismissPasteDialog()
        }
    }

    fun dismissPasteDialog() {
        _uiState.value = _uiState.value.copy(
            showPasteDialog = false,
            parsedPasteItems = emptyList()
        )
    }

    fun setRecentSheetVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showRecentSheet = visible)
    }

    fun toggleCompletedSection() {
        _uiState.value = _uiState.value.copy(
            showCompletedSection = !_uiState.value.showCompletedSection
        )
    }
}
