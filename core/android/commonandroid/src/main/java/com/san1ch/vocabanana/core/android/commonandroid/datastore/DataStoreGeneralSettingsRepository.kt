package com.san1ch.vocabanana.core.android.commonandroid.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.san1ch.vocabanana.core.essentials.model.AppThemeMode
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class DataStoreGeneralSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    private object Keys {
        val INIT_ACTIVE = booleanPreferencesKey("init_active")
        val THEME = stringPreferencesKey("theme")
        val LAST_DB_UPDATE_TIME = longPreferencesKey("last_db_update_time")
    }

    override val initActiveFlow: Flow<Boolean> =
        dataStore.data.map { it[Keys.INIT_ACTIVE] ?: true }

    override suspend fun setInitActive(value: Boolean) {
        dataStore.edit { it[Keys.INIT_ACTIVE] = value }
    }

    override val themeFlow: Flow<AppThemeMode> = dataStore.data.map { preferences ->
        val themeName = preferences[Keys.THEME]
            ?: AppThemeMode.AUTO.name
        return@map try {
            AppThemeMode.valueOf(themeName)
        } catch (e: Exception) {
            AppThemeMode.AUTO
        }
    }

    override suspend fun setTheme(value: AppThemeMode) {
        dataStore.edit { it[Keys.THEME] = value.name }
    }

    override val lastDatabaseUpdateTime: Flow<Long>
        get() = dataStore.data.map { it[Keys.LAST_DB_UPDATE_TIME] ?: 0L }

    override suspend fun setLastDatabaseUpdateTime(time: Long) {
        dataStore.edit { it[Keys.LAST_DB_UPDATE_TIME] = time }
    }
}
