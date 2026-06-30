package com.san1ch.vocabanana.core.essentials.database.exceptions

import com.san1ch.vocabanana.core.essentials.database.resources.StringProviderStore

interface WithLocalizedMessage {
    fun getLocalizedMessage(stringProviderStore: StringProviderStore): String
}