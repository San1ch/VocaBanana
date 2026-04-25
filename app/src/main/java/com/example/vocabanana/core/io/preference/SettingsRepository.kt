package com.example.vocabanana.core.io.preference

import com.example.vocabanana.core.presentation.settings.AppTheme
import com.example.vocabanana.feature.text.presentation.textlistscreenpages.ReaderSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val initActiveFlow: Flow<Boolean>
    suspend fun setInitActive(value: Boolean)

    val themeFlow: Flow<AppTheme>
    suspend fun setTheme(value: AppTheme)

    val readerSettingsFlow: Flow<ReaderSettings>
    suspend fun saveReaderSettings(settings: ReaderSettings)
}