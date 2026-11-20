package com.savoj.shoppinglist.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getTheme(): Flow<String>
    suspend fun setTheme(theme: String)

    fun getLanguage(): Flow<String>
    suspend fun setLanguage(languageCode: String)
}
