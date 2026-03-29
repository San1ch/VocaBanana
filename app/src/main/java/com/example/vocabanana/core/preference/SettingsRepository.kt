package com.example.vocabanana.core.preference

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val initActiveFlow: Flow<Boolean>
    suspend fun getInitActive(): Boolean
    suspend fun setInitActive(value: Boolean)
}