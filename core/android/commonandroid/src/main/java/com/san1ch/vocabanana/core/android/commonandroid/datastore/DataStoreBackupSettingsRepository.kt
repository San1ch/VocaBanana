package com.san1ch.vocabanana.core.android.commonandroid.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.san1ch.vocabanana.core.essentials.repositories.BackupSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreBackupSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : BackupSettingsRepository {

    private object Keys {
        val IS_LOCAL_ENABLED = booleanPreferencesKey("is_local_enabled")
        val LOCAL_BACKUP_PATH = stringPreferencesKey("local_backup_path")
        val LAST_LOCAL_BACKUP_TIME = longPreferencesKey("last_local_backup_time")
        val LAST_CLOUD_BACKUP_TIME = longPreferencesKey("last_cloud_backup_time")

        val LAST_DB_UPDATE_TIME = longPreferencesKey("last_db_update_time")
    }

    // Local Backup
    override val localBackupPathFlow: Flow<String> =
        dataStore.data.map { it[Keys.LOCAL_BACKUP_PATH] ?: "" }

    override suspend fun setLocalBackupPath(path: String) {
        dataStore.edit { it[Keys.LOCAL_BACKUP_PATH] = path }
    }

    override suspend fun clearLocalBackupPath() {
        dataStore.edit { it.remove(Keys.LOCAL_BACKUP_PATH) }
    }

    override val isLocalBackupEnabledFlow: Flow<Boolean> =
        dataStore.data.map { it[Keys.IS_LOCAL_ENABLED] ?: false }

    override suspend fun setLocalBackupEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.IS_LOCAL_ENABLED] = enabled }
    }

    // Timestamps
    override val lastLocalBackupTimeFlow: Flow<Long> =
        dataStore.data.map { it[Keys.LAST_LOCAL_BACKUP_TIME] ?: 0L }

    override suspend fun setLastLocalBackupTime(time: Long) {
        dataStore.edit { it[Keys.LAST_LOCAL_BACKUP_TIME] = time }
    }

    override val lastCloudBackupTimeFlow: Flow<Long> =
        dataStore.data.map { it[Keys.LAST_CLOUD_BACKUP_TIME] ?: 0L }

    override suspend fun setLastCloudBackupTime(time: Long) {
        dataStore.edit { it[Keys.LAST_CLOUD_BACKUP_TIME] = time }
    }

    // Update flags
    private val lastDatabaseUpdateTime: Flow<Long> =
        dataStore.data.map { it[Keys.LAST_DB_UPDATE_TIME] ?: 0L }

    override val isLocalBackupNeedUpdateFlow: Flow<Boolean> = combine(
        lastLocalBackupTimeFlow,
        lastDatabaseUpdateTime,
    ) { lastLocalBackupTime, lastDatabaseUpdateTime ->
        lastDatabaseUpdateTime > lastLocalBackupTime
    }

    override val isCloudBackupNeedUpdateFlow: Flow<Boolean> = combine(
        lastCloudBackupTimeFlow,
        lastDatabaseUpdateTime,
    ) { lastCloudBackupTime, lastDatabaseUpdateTime ->
        lastDatabaseUpdateTime > lastCloudBackupTime
    }
}
