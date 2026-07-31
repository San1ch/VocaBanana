package com.san1ch.vocabanana.feature.backup.presentation

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.san1ch.vocabanana.core.essentials.DataChangeTracker
import com.san1ch.vocabanana.core.essentials.backup.LocalBackupManager
import com.san1ch.vocabanana.core.essentials.repositories.BackupSettingsRepository
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class LocalBackupManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val backupSettingsRepository: BackupSettingsRepository,
    private val settingsRepository: SettingsRepository,
    private val dataChangeTracker: DataChangeTracker,
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
            ZipOutputStream(outputStream).use { zipOut ->
                val databasesDir = File(context.applicationInfo.dataDir, "databases")
                if (databasesDir.exists()) {
                    databasesDir.listFiles()?.forEach { file ->
                        zipFileRecursive(file, "databases/${file.name}", zipOut)
                    }
                }

                val filesDir = context.filesDir
                if (filesDir.exists()) {
                    filesDir.listFiles()?.forEach { file ->
                        zipFileRecursive(file, "files/${file.name}", zipOut)
                    }
                }

                val metadataJson = JSONObject().apply {
                    put("backupTimestamp", System.currentTimeMillis())
                    put("appVersionCode", 1)
                }.toString()

                zipOut.putNextEntry(ZipEntry("metadata.json"))
                zipOut.write(metadataJson.toByteArray(Charsets.UTF_8))
                zipOut.closeEntry()
            }
        } ?: throw IllegalStateException("Could not open output stream for file URI: $fileUri")

        backupSettingsRepository.setLastBackupTime(System.currentTimeMillis())
    }

    override suspend fun restore(fileUriString: String) = withContext(Dispatchers.IO) {
        val uri = fileUriString.toUri()

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            ZipInputStream(inputStream).use { zipIn ->
                val dataDir = File(context.applicationInfo.dataDir)

                var entry = zipIn.nextEntry
                while (entry != null) {
                    if (entry.name == "metadata.json") {
                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                        continue
                    }

                    val targetFile = File(dataDir, entry.name)

                    if (entry.isDirectory) {
                        targetFile.mkdirs()
                    } else {
                        targetFile.parentFile?.mkdirs()
                        FileOutputStream(targetFile).use { fos ->
                            zipIn.copyTo(fos)
                        }
                    }

                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
                dataChangeTracker.notifyDataChanged()
                backupSettingsRepository.setLastBackupTime(System.currentTimeMillis())
            }
        } ?: throw IllegalStateException("Could not open input stream for URI: $fileUriString")
    }

    private fun zipFileRecursive(file: File, entryName: String, zipOut: ZipOutputStream) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                zipFileRecursive(child, "$entryName/${child.name}", zipOut)
            }
        } else {
            zipOut.putNextEntry(ZipEntry(entryName))
            FileInputStream(file).use { fis ->
                fis.copyTo(zipOut)
            }
            zipOut.closeEntry()
        }
    }
}
