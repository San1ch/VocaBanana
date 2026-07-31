package com.san1ch.vocabanana.core.essentials.backup

interface CloudBackupManager {
    suspend fun backup(email: String)
    suspend fun restore(email: String)
}
