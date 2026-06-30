package com.san1ch.vocabanana.core.essentials.database.exceptions.mapper

import com.san1ch.vocabanana.core.essentials.database.exceptions.WithLocalizedMessage
import com.san1ch.vocabanana.core.essentials.database.resources.CoreStringProvider
import com.san1ch.vocabanana.core.essentials.database.resources.StringProviderStore
import javax.inject.Inject

class DefaultExceptionToMessageMapper @Inject constructor(
    private val stringProviderStore: StringProviderStore
) : ExceptionToMessageMapper {

    override fun getLocalizedMessage(exception: Exception): String {
        return if(exception is WithLocalizedMessage){
            exception.getLocalizedMessage(stringProviderStore)
        } else {
            stringProviderStore<CoreStringProvider>().unknownErrorMessage
        }
    }

}