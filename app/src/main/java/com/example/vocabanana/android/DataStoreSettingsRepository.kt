package com.example.vocabanana.android

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.vocabanana.core.io.preference.SettingsRepository
import com.example.vocabanana.core.presentation.settings.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    companion object {
        val INIT_ACTIVE = booleanPreferencesKey("init_active")
        val THEME = stringPreferencesKey("theme")
    }

    //InitActive
    override val initActiveFlow: Flow<Boolean> =
        dataStore.data.map { it[INIT_ACTIVE] ?: true }
    override suspend fun getInitActive(): Boolean =
        initActiveFlow.first()
    override suspend fun setInitActive(value: Boolean) {
        dataStore.edit { it[INIT_ACTIVE] = value } }

    //Theme
    override val themeFlow: Flow<AppTheme> = dataStore.data.map { preferences ->
        val themeName = preferences[THEME] ?: AppTheme.AUTO.name
        try {
            AppTheme.valueOf(themeName)
        } catch (e: Exception) {
            AppTheme.AUTO

        }
    }

    override suspend fun getTheme(): AppTheme =
        themeFlow.first()

    override suspend fun setTheme(value: AppTheme) {
        dataStore.edit { it[THEME] = value.name }
    }


}