package com.example.vocabanana.core.io.preference

import com.example.vocabanana.core.presentation.settings.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val initActiveFlow: Flow<Boolean>
    suspend fun getInitActive(): Boolean
    suspend fun setInitActive(value: Boolean)

    val themeFlow: Flow<AppTheme>
    suspend fun getTheme(): AppTheme
    suspend fun setTheme(value: AppTheme)
}