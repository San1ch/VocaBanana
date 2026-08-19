package com.san1ch.vocabanana.feature.backup.presentation.stringprovider

import android.content.Context
import android.text.format.DateFormat
import com.san1ch.vocabanana.core.essentials.resources.featureproviders.BackupStringProvider
import com.san1ch.vocabanana.feature.backup.presentation.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BackupStringProviderImpl @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
) : BackupStringProvider {

    private val dateFormat = DateFormat.getLongDateFormat(context)

    override fun lastBackupTimeMessage(lastBackupTime: Long): String = context.getString(R.string.last_backup_time, dateFormat.format(lastBackupTime))

    override fun backupSuccessMessage(): String = context.getString(R.string.backup_success)

    override fun loadSuccessMessage(): String = context.getString(R.string.load_success)
    override fun driveConsentRequiredMessage(): String = context.getString(R.string.drive_consent_required)
}
