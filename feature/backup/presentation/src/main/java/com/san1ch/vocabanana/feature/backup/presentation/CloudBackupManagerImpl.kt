package com.san1ch.vocabanana.feature.backup.presentation

import com.san1ch.vocabanana.core.essentials.backup.CloudBackupManager
import javax.inject.Inject

class CloudBackupManagerImpl @Inject constructor() : CloudBackupManager {
    override suspend fun backup(email: String) {
        TODO("Not yet implemented")
    }

    override suspend fun restore(email: String) {
        TODO("Not yet implemented")
    }
}
