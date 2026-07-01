package com.san1ch.vocabanana.core.essentials.model.word.exception

import com.san1ch.vocabanana.core.essentials.exceptions.AppException
import com.san1ch.vocabanana.core.essentials.exceptions.WithLocalizedMessage
import com.san1ch.vocabanana.core.essentials.resources.StringProviderStore
import com.san1ch.vocabanana.core.essentials.resources.featureproviders.WordStringProvider

abstract class WordException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause), WithLocalizedMessage {
    override fun getLocalizedMessage(stringProviderStore: StringProviderStore): String {
        return getLocalizedMessage(stringProviderStore<WordStringProvider>() )
    }
    abstract fun getLocalizedMessage(stringProvider: WordStringProvider): String
}


class WordValidateEmptyException: WordException("Word is empty"){
    override fun getLocalizedMessage(stringProvider: WordStringProvider): String {
        return stringProvider.wordEmpty
    }
}
class WordValidateTooLongException(private val maxSize: Int) : WordException("Word is too long"){
    override fun getLocalizedMessage(stringProvider: WordStringProvider): String {
        return stringProvider.wordTooLong(maxSize)
    }
}
class WordValidateInvalidLemmaCharException(private val invalidChar: Char) : WordException("Word contains invalid lemma char"){
    override fun getLocalizedMessage(stringProvider: WordStringProvider): String {
        return stringProvider.wordInvalidLemmaChar(invalidChar)
    }
}