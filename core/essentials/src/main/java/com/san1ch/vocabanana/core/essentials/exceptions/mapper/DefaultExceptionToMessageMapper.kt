package com.san1ch.vocabanana.core.essentials.exceptions.mapper

import com.san1ch.vocabanana.core.essentials.exceptions.WithLocalizedMessage
import com.san1ch.vocabanana.core.essentials.resources.CoreStringProvider
import com.san1ch.vocabanana.core.essentials.resources.StringProviderStore
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