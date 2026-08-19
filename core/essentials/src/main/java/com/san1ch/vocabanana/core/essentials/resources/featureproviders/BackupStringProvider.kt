package com.san1ch.vocabanana.core.essentials.resources.featureproviders

import com.san1ch.vocabanana.core.essentials.resources.StringProvider

interface BackupStringProvider : StringProvider {
    fun lastBackupTimeMessage(lastBackupTime: Long): String
    fun backupSuccessMessage(): String
    fun loadSuccessMessage(): String
    fun driveConsentRequiredMessage(): String
}
