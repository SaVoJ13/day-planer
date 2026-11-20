package com.savoj.shoppinglist.ui

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    fun updateLocale(context: Context, languageCode: String): Context {
        if (languageCode == "system" || languageCode.isBlank()) {
            return context
        }
        val locale = getLocale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
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
