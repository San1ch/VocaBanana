package com.san1ch.vocabanana.core.android.commonandroid.datastore

import androidx.datastore.preferences.core.Preferences
import com.san1ch.vocabanana.core.android.commonandroid.datastore.SettingsKeys.THEME
import com.san1ch.vocabanana.core.essentials.model.AppThemeMode

object DataStoreSettingsMapper {

    fun mapTheme(preferences: Preferences): AppThemeMode {
        val themeName = preferences[THEME]
            ?: AppThemeMode.AUTO.name
        return try {
            AppThemeMode.valueOf(themeName)
        } catch (e: Exception) {
            AppThemeMode.AUTO
        }
    }
}
