package com.san1ch.vocabanana.core.essentials.database.exceptions

import com.san1ch.vocabanana.core.essentials.database.resources.StringProviderStore
import com.san1ch.vocabanana.core.essentials.database.resources.featureproviders.RepositoryStringProvider


abstract class RepositoryException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause), WithLocalizedMessage{
    override fun getLocalizedMessage(stringProviderStore: StringProviderStore): String {
        return getLocalizedMessage(stringProviderStore<RepositoryStringProvider>() )
    }
    abstract fun getLocalizedMessage(stringProvider: RepositoryStringProvider): String
}


class RepositoryNoDataByRequestException: RepositoryException("No data by request"){
    override fun getLocalizedMessage(stringProvider: RepositoryStringProvider): String {
        return stringProvider.thereIsNoDataByRequest
    }
}