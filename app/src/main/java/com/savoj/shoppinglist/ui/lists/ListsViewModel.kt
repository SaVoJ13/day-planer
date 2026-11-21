package com.savoj.shoppinglist.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savoj.shoppinglist.data.remote.GroupShoppingRepository
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
    val showJoinDialog: Boolean = false,
    val isJoining: Boolean = false,
    val listToEdit: ShoppingList? = null,
    val listToDelete: ShoppingList? = null,
    val listToShare: ShoppingList? = null,
    val toastMessage: String? = null,
    val errorMessage: String? = null
)

class ListsViewModel(
    private val repository: ShoppingRepository,
    private val groupRepository: GroupShoppingRepository? = null
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

    fun showJoinDialog() {
        _uiState.value = _uiState.value.copy(showJoinDialog = true, errorMessage = null)
    }

    fun hideJoinDialog() {
        _uiState.value = _uiState.value.copy(showJoinDialog = false, errorMessage = null)
    }

    fun joinSharedList(shareCode: String, onJoined: (Long) -> Unit) {
        if (groupRepository == null) return
        val code = shareCode.trim()
        if (code.length != 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Код має містити 6 символів")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isJoining = true, errorMessage = null)
            val result = groupRepository.joinSharedList(code)
            _uiState.value = _uiState.value.copy(isJoining = false)
            result.onSuccess { localId ->
                hideJoinDialog()
                onJoined(localId)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = err.message ?: "Не вдалося приєднатися до списку"
                )
            }
        }
    }

    fun showShareDialog(list: ShoppingList) {
        if (list.isShared && !list.shareCode.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(listToShare = list)
        } else {
            createAndShareList(list)
        }
    }

    fun hideShareDialog() {
        _uiState.value = _uiState.value.copy(listToShare = null)
    }

    fun createAndShareList(list: ShoppingList) {
        if (groupRepository == null) return
        viewModelScope.launch {
            val result = groupRepository.createSharedList(list.id, list.name, emptyList())
            result.onSuccess { code ->
                _uiState.value = _uiState.value.copy(
                    listToShare = list.copy(isShared = true, shareCode = code)
                )
            }
        }
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

    fun toggleListLocked(list: ShoppingList) {
        viewModelScope.launch {
            repository.setListLocked(list.id, !list.isLocked)
        }
    }
}
