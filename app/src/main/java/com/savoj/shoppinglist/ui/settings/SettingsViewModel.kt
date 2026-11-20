package com.savoj.shoppinglist.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savoj.shoppinglist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val theme: StateFlow<String> = repository.getTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val language: StateFlow<String> = repository.getLanguage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    fun setTheme(theme: String) {
        viewModelScope.launch {
            repository.setTheme(theme)
        }
    }

    fun setLanguage(languageCode: String) {
        viewModelScope.launch {
            repository.setLanguage(languageCode)
        }
    }
}
