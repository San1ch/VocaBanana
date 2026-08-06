package com.san1ch.vocabanana.core.ui.exception

import android.app.PendingIntent
import com.san1ch.vocabanana.core.essentials.exceptions.BackupException
import com.san1ch.vocabanana.core.essentials.resources.featureproviders.BackupStringProvider

class DriveConsentRequiredException(
    val pendingIntent: PendingIntent,
    cause: Throwable? = null,
) : BackupException("User consent required", cause) {
    override fun getLocalizedMessage(
        stringProvider: BackupStringProvider,
    ): String = stringProvider.driveConsentRequiredMessage()
}
