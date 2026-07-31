package com.san1ch.vocabanana.core.android.commonandroid.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.san1ch.vocabanana.core.android.commonandroid.datastore.SettingsKeys.CLOUD_EMAIL
import com.san1ch.vocabanana.core.android.commonandroid.datastore.SettingsKeys.INIT_ACTIVE
import com.san1ch.vocabanana.core.android.commonandroid.datastore.SettingsKeys.IS_CLOUD_ENABLED
import com.san1ch.vocabanana.core.android.commonandroid.datastore.SettingsKeys.IS_LOCAL_ENABLED
import com.san1ch.vocabanana.core.android.commonandroid.datastore.SettingsKeys.LAST_BACKUP_TIME
import com.san1ch.vocabanana.core.android.commonandroid.datastore.SettingsKeys.LAST_DB_UPDATE_TIME
import com.san1ch.vocabanana.core.android.commonandroid.datastore.SettingsKeys.LOCAL_BACKUP_PATH
import com.san1ch.vocabanana.core.android.commonandroid.datastore.SettingsKeys.THEME
import com.san1ch.vocabanana.core.essentials.model.AppThemeMode
import com.san1ch.vocabanana.core.essentials.repositories.BackupSettingsRepository
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object SettingsKeys {
    val INIT_ACTIVE = booleanPreferencesKey("init_active")
    val THEME = stringPreferencesKey("theme")

    val IS_LOCAL_ENABLED = booleanPreferencesKey("is_local_enabled")
    val LOCAL_BACKUP_PATH = stringPreferencesKey("local_backup_path")

    val IS_CLOUD_ENABLED = booleanPreferencesKey("is_cloud_enabled")
    val CLOUD_EMAIL = stringPreferencesKey("cloud_email")

    val LAST_DB_UPDATE_TIME = longPreferencesKey("last_db_update_time")
    val LAST_BACKUP_TIME = longPreferencesKey("last_backup_time")
}

class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository,
    BackupSettingsRepository {

    // --- InitActive ---
    override val initActiveFlow: Flow<Boolean> =
        dataStore.data.map { it[INIT_ACTIVE] ?: true }

    override suspend fun setInitActive(value: Boolean) {
        dataStore.edit { it[INIT_ACTIVE] = value }
    }

    // --- Theme ---
    override val themeFlow: Flow<AppThemeMode> = dataStore.data.map { preferences ->
        DataStoreSettingsMapper.mapTheme(preferences)
    }

    override suspend fun setTheme(value: AppThemeMode) {
        dataStore.edit { it[THEME] = value.name }
    }

    // --- Local Backup ---
    override val localBackupPathFlow: Flow<String> =
        dataStore.data.map { it[LOCAL_BACKUP_PATH] ?: "" }

    override suspend fun setLocalBackupPath(path: String) {
        dataStore.edit { it[LOCAL_BACKUP_PATH] = path }
    }

    override val isLocalBackupEnabledFlow: Flow<Boolean> =
        dataStore.data.map { it[IS_LOCAL_ENABLED] ?: false }

    override suspend fun setLocalBackupEnabled(enabled: Boolean) {
        dataStore.edit { it[IS_LOCAL_ENABLED] = enabled }
    }

    // --- Cloud Backup ---
    override val cloudEmailFlow: Flow<String> =
        dataStore.data.map { it[CLOUD_EMAIL] ?: "" }

    override suspend fun setCloudEmail(email: String) {
        dataStore.edit { it[CLOUD_EMAIL] = email }
    }

    override val isCloudBackupEnabledFlow: Flow<Boolean> =
        dataStore.data.map { it[IS_CLOUD_ENABLED] ?: false }

    override suspend fun setCloudBackupEnabled(enabled: Boolean) {
        dataStore.edit { it[IS_CLOUD_ENABLED] = enabled }
    }

    // --- Database Update Time ---
    override val lastDatabaseUpdateTime: Flow<Long> =
        dataStore.data.map { it[LAST_DB_UPDATE_TIME] ?: 0L }

    override suspend fun setLastDatabaseUpdateTime(time: Long) {
        dataStore.edit { it[LAST_DB_UPDATE_TIME] = time }
    }

    // --- Last Backup Time ---
    override val lastBackupTimeFlow: Flow<Long> =
        dataStore.data.map { it[LAST_BACKUP_TIME] ?: 0L }

    override suspend fun setLastBackupTime(time: Long) {
        dataStore.edit { it[LAST_BACKUP_TIME] = time }
    }

    override val isBackupNeedUpdateFlow: Flow<Boolean> = combine(
        lastBackupTimeFlow,
        lastDatabaseUpdateTime,
    ) { lastBackupTime, lastDatabaseUpdateTime ->
        println("lastBackupTime: $lastBackupTime, lastDatabaseUpdateTime: $lastDatabaseUpdateTime")
        lastDatabaseUpdateTime > lastBackupTime
    }
}
