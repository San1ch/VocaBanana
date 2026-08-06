package com.san1ch.vocabanana.feature.backup.presentation

import android.content.Context
import com.san1ch.vocabanana.core.essentials.DataChangeTracker
import com.san1ch.vocabanana.core.essentials.backup.CloudBackupManager
import com.san1ch.vocabanana.core.essentials.network.CloudStorageClient
import com.san1ch.vocabanana.core.essentials.repositories.BackupSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class CloudBackupManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val archiver: BackupArchiver,
    private val dataChangeTracker: DataChangeTracker,
    private val backupSettingsRepository: BackupSettingsRepository,
    private val cloudStorageClient: CloudStorageClient,
) : CloudBackupManager {

    override suspend fun backup(): Result<Unit> = runCatching {
        val backupFile = File(context.cacheDir, "vocabanana_backup.zip")

        try {
            backupFile.outputStream().use { outputStream ->
                archiver.createBackupZip(outputStream)
            }
            cloudStorageClient.uploadOrUpdateBackup(backupFile).getOrThrow()
            backupSettingsRepository.setLastCloudBackupTime(System.currentTimeMillis())
        } finally {
            if (backupFile.exists()) {
                backupFile.delete()
            }
        }
    }

    override suspend fun restore(): Result<Unit> = runCatching {
        val downloadedFile = cloudStorageClient.downloadBackup().getOrThrow()

        try {
            downloadedFile.inputStream().use { inputStream ->
                archiver.restoreFromBackupZip(inputStream)
            }

            dataChangeTracker.notifyDataChanged()
            backupSettingsRepository.setLastCloudBackupTime(System.currentTimeMillis())
        } finally {
            if (downloadedFile.exists()) {
                downloadedFile.delete()
            }
        }
    }
}
