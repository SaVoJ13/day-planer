package com.savoj.shoppinglist.ui

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import java.util.Locale

object LocaleHelper {
    fun updateLocale(context: Context, languageCode: String): Context {
        val locale = if (languageCode == "system" || languageCode.isBlank()) {
            val systemLocales = Resources.getSystem().configuration.locales
            if (!systemLocales.isEmpty) systemLocales.get(0) else Locale.getDefault()
        } else {
            getLocale(languageCode)
        }
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLocales(LocaleList(locale))
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    fun getLocale(languageCode: String): Locale {
        return when (languageCode) {
            "en" -> Locale.forLanguageTag("en")
            "pl" -> Locale.forLanguageTag("pl")
            "ru" -> Locale.forLanguageTag("ru")
            else -> Locale.forLanguageTag("uk")
        }
    }
}
