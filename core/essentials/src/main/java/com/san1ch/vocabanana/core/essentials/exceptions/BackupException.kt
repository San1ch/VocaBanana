package com.san1ch.vocabanana.core.essentials.exceptions

import com.san1ch.vocabanana.core.essentials.resources.StringProviderStore
import com.san1ch.vocabanana.core.essentials.resources.featureproviders.BackupStringProvider

abstract class BackupException(
    message: String,
    cause: Throwable? = null,
) : AppException(message, cause),
    WithLocalizedMessage {
    override fun getLocalizedMessage(
        stringProviderStore: StringProviderStore,
    ): String = getLocalizedMessage(stringProviderStore<BackupStringProvider>())

    abstract fun getLocalizedMessage(stringProvider: BackupStringProvider): String
}
