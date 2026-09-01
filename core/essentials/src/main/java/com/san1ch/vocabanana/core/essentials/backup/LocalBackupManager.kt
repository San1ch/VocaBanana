package com.san1ch.vocabanana.core.essentials.backup

import com.san1ch.vocabanana.core.essentials.model.ResultWithState

interface LocalBackupManager {
    suspend fun backup(): ResultWithState<Unit, Unit>
    suspend fun restore(fileUriString: String): ResultWithState<Unit, Unit>
}
