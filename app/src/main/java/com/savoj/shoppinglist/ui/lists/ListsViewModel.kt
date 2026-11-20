package com.savoj.shoppinglist.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savoj.shoppinglist.domain.model.ShoppingList
import com.savoj.shoppinglist.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ListsUiState(
    val lists: List<ShoppingList> = emptyList(),
    val showCreateDialog: Boolean = false,
    val listToEdit: ShoppingList? = null,
    val listToDelete: ShoppingList? = null,
    val toastMessage: String? = null
)

class ListsViewModel(
    private val repository: ShoppingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListsUiState())
    val uiState: StateFlow<ListsUiState> = _uiState.asStateFlow()

    val lists: StateFlow<List<ShoppingList>> = repository.getAllLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun showEditDialog(list: ShoppingList) {
        _uiState.value = _uiState.value.copy(listToEdit = list)
    }

    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(listToEdit = null)
    }

    fun showDeleteDialog(list: ShoppingList) {
        _uiState.value = _uiState.value.copy(listToDelete = list)
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(listToDelete = null)
    }

    fun createList(name: String, colorHex: String = "#4CAF50") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createList(name, colorHex)
            hideCreateDialog()
        }
    }

    fun updateList(list: ShoppingList, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.updateList(list.copy(name = newName.trim()))
            hideEditDialog()
        }
    }

    fun deleteList(listId: Long) {
        viewModelScope.launch {
            repository.deleteList(listId)
            hideDeleteDialog()
        }
    }

    fun resetAllItems(listId: Long) {
        viewModelScope.launch {
            repository.resetAllItemsAsIncomplete(listId)
        }
    }
}
