package com.san1ch.vocabanana.core.essentials.repositories

import kotlinx.coroutines.flow.Flow

interface BackupSettingsRepository {
    val localBackupPathFlow: Flow<String>
    suspend fun setLocalBackupPath(path: String)

    val isLocalBackupEnabledFlow: Flow<Boolean>
    suspend fun setLocalBackupEnabled(enabled: Boolean)

    val isCloudBackupEnabledFlow: Flow<Boolean>
    suspend fun setCloudBackupEnabled(enabled: Boolean)

    val lastLocalBackupTimeFlow: Flow<Long>
    suspend fun setLastLocalBackupTime(time: Long)

    val lastCloudBackupTimeFlow: Flow<Long>
    suspend fun setLastCloudBackupTime(time: Long)

    val isLocalBackupNeedUpdateFlow: Flow<Boolean>
    val isCloudBackupNeedUpdateFlow: Flow<Boolean>
}
