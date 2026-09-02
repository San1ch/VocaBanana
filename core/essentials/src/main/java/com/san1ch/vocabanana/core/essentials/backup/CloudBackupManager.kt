package com.san1ch.vocabanana.core.essentials.backup

import com.san1ch.vocabanana.core.essentials.model.ResultWithState

interface CloudBackupManager {
    suspend fun backup(): ResultWithState<Unit, Unit>
    suspend fun restore(): ResultWithState<Unit, Unit>
    suspend fun deleteCloudBackup(): ResultWithState<Unit, Unit>
}
