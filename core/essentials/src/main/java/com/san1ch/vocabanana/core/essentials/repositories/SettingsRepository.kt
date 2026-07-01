package com.san1ch.vocabanana.core.essentials.repositories

import com.san1ch.vocabanana.core.essentials.model.AppThemeMode
import com.san1ch.vocabanana.core.essentials.model.ReaderSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val initActiveFlow: Flow<Boolean>
    suspend fun setInitActive(value: Boolean)

    val themeFlow: Flow<AppThemeMode>
    suspend fun setTheme(value: AppThemeMode)

    val readerSettingsFlow: Flow<ReaderSettings>
    suspend fun saveReaderSettings(settings: ReaderSettings)
}