package com.san1ch.vocabanana.core.essentials.backup

interface LocalBackupManager {
    suspend fun backup()
    suspend fun restore(fileUriString: String)
}
