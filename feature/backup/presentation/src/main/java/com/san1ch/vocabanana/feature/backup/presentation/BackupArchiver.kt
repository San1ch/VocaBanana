package com.san1ch.vocabanana.feature.backup.presentation

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class BackupArchiver @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    suspend fun createBackupZip(outputStream: OutputStream) = withContext(Dispatchers.IO) {
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
                    // Exclude datastore files
                    if (file.name == "datastore" || file.name.contains("datastore")) {
                        return@forEach
                    }

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
    }

    suspend fun restoreFromBackupZip(inputStream: InputStream) = withContext(Dispatchers.IO) {
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
        }
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
