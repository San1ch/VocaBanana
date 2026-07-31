package com.san1ch.vocabanana.core.essentials.resources

interface BackupStringProvider {
    fun lastBackupTimeMessage(lastBackupTime: Long): String
    fun backupSuccessMessage(): String
    fun loadSuccessMessage(): String
}
