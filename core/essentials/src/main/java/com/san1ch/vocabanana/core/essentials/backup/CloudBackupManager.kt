package com.san1ch.vocabanana.core.essentials.backup

interface CloudBackupManager {
    suspend fun backup(): Result<Unit>
    suspend fun restore(): Result<Unit>
}
