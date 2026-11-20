package com.savoj.shoppinglist.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.savoj.shoppinglist.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SettingsRepositoryImpl(
    context: Context
) : SettingsRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override fun getTheme(): Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_THEME) {
                trySend(prefs.getString(KEY_THEME, THEME_SYSTEM) ?: THEME_SYSTEM)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getString(KEY_THEME, THEME_SYSTEM) ?: THEME_SYSTEM)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setTheme(theme: String) {
        prefs.edit().putString(KEY_THEME, theme).apply()
    }

    override fun getLanguage(): Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_LANGUAGE) {
                trySend(prefs.getString(KEY_LANGUAGE, LANG_SYSTEM) ?: LANG_SYSTEM)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getString(KEY_LANGUAGE, LANG_SYSTEM) ?: LANG_SYSTEM)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setLanguage(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    companion object {
        const val KEY_THEME = "pref_theme"
        const val KEY_LANGUAGE = "pref_language"

        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"

        const val LANG_SYSTEM = "system"
        const val LANG_UK = "uk"
        const val LANG_EN = "en"
        const val LANG_PL = "pl"
        const val LANG_RU = "ru"
    }
}
