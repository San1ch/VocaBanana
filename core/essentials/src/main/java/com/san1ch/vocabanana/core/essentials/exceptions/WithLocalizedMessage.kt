package com.san1ch.vocabanana.core.essentials.exceptions

import com.san1ch.vocabanana.core.essentials.resources.StringProviderStore

interface WithLocalizedMessage {
    fun getLocalizedMessage(stringProviderStore: StringProviderStore): String
}