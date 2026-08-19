package com.san1ch.vocabanana.core.essentials.network

import java.io.File

interface CloudStorageClient {
    suspend fun uploadOrUpdateBackup(backupFile: File): Result<Unit>
    suspend fun downloadBackup(): Result<File>
}
