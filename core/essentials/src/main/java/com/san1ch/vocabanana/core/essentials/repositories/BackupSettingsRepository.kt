package com.san1ch.vocabanana.core.essentials.repositories

import kotlinx.coroutines.flow.Flow

interface BackupSettingsRepository {
    val localBackupPathFlow: Flow<String>
    suspend fun setLocalBackupPath(path: String)

    val isLocalBackupEnabledFlow: Flow<Boolean>
    suspend fun setLocalBackupEnabled(enabled: Boolean)

    val cloudEmailFlow: Flow<String>
    suspend fun setCloudEmail(email: String)

    val isCloudBackupEnabledFlow: Flow<Boolean>
    suspend fun setCloudBackupEnabled(enabled: Boolean)

    val lastBackupTimeFlow: Flow<Long>
    suspend fun setLastBackupTime(time: Long)

    val isBackupNeedUpdateFlow: Flow<Boolean>
}
