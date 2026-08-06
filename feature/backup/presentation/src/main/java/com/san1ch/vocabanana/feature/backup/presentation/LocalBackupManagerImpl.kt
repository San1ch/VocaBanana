package com.san1ch.vocabanana.feature.backup.presentation

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.san1ch.vocabanana.core.essentials.DataChangeTracker
import com.san1ch.vocabanana.core.essentials.backup.LocalBackupManager
import com.san1ch.vocabanana.core.essentials.repositories.BackupSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalBackupManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val backupSettingsRepository: BackupSettingsRepository,
    private val dataChangeTracker: DataChangeTracker,
    private val backupArchiver: BackupArchiver,
) : LocalBackupManager {

    override suspend fun backup() = withContext(Dispatchers.IO) {
        val folderUriString = backupSettingsRepository.localBackupPathFlow.first()

        if (folderUriString.isBlank() || !folderUriString.startsWith("content://")) {
            throw IllegalStateException("Invalid backup URI: '$folderUriString'")
        }

        val treeUri = folderUriString.toUri()

        val documentFile = DocumentFile.fromTreeUri(context, treeUri)
            ?: throw IllegalStateException("Could not access document tree from URI: $treeUri")

        val existingFile = documentFile.findFile("vocabanana_backup.zip")
        val fileUri = existingFile?.uri ?: documentFile.createFile("application/zip", "vocabanana_backup.zip")?.uri
            ?: throw IllegalStateException("Could not create backup file in the selected folder")

        context.contentResolver.openOutputStream(fileUri, "wt")?.use { outputStream ->
            backupArchiver.createBackupZip(outputStream)
        } ?: throw IllegalStateException("Could not open output stream for file URI: $fileUri")

        backupSettingsRepository.setLastLocalBackupTime(System.currentTimeMillis())
    }

    override suspend fun restore(fileUriString: String) = withContext(Dispatchers.IO) {
        val uri = fileUriString.toUri()

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            backupArchiver.restoreFromBackupZip(inputStream)
            dataChangeTracker.notifyDataChanged()
            backupSettingsRepository.setLastLocalBackupTime(System.currentTimeMillis())
        } ?: throw IllegalStateException("Could not open input stream for URI: $fileUriString")
    }
}
