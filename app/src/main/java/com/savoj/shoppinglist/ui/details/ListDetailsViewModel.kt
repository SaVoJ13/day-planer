package com.savoj.shoppinglist.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val showCompletedSection: Boolean = true
)

class ListDetailsViewModel(
    private val listId: Long,
    private val repository: ShoppingRepository
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

    fun addItem(name: String, quantity: Double? = null, unit: String? = null, categoryId: Long? = null) {
        val cleanName = ShoppingRepositoryImpl.cleanItemName(name)
        if (cleanName.isBlank()) return
        viewModelScope.launch {
            repository.addItem(listId, cleanName, quantity, unit, categoryId)
        }
    }

    fun toggleItemCompleted(item: ShoppingItem) {
        viewModelScope.launch {
            repository.updateItem(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(lastDeletedItem = item)
            repository.deleteItem(item.id)
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
