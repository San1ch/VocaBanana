package com.san1ch.vocabanana.core.essentials.model.text.exception

import com.san1ch.vocabanana.core.essentials.exceptions.AppException
import com.san1ch.vocabanana.core.essentials.exceptions.WithLocalizedMessage
import com.san1ch.vocabanana.core.essentials.resources.StringProviderStore
import com.san1ch.vocabanana.core.essentials.resources.featureproviders.TextStringProvider


abstract class TextException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause), WithLocalizedMessage {
    override fun getLocalizedMessage(stringProviderStore: StringProviderStore): String {
        return getLocalizedMessage(stringProviderStore<TextStringProvider>())
    }

    abstract fun getLocalizedMessage(stringProvider: TextStringProvider): String
}


class TextValidateEmptyNameException : TextException("Text name is empty") {
    override fun getLocalizedMessage(stringProvider: TextStringProvider): String {
        return stringProvider.textValidateEmptyName
    }
}

class TextValidateEmptyTextException : TextException("Text is empty") {
    override fun getLocalizedMessage(stringProvider: TextStringProvider): String {
        return stringProvider.textValidateEmptyName
    }
}

class TextValidateTooLongException(private val maxSize: Int) :
    TextException("Text name is too long") {
    override fun getLocalizedMessage(stringProvider: TextStringProvider): String {
        return stringProvider.textNameValidateTooLong(maxSize)
    }
}

class TextValidateNameHasInvalidCharException(
    private val invalidChar: Char
) : TextException("Text name contains invalid char") {
    override fun getLocalizedMessage(stringProvider: TextStringProvider): String {
        return stringProvider.textNameValidateHasInvalidChar(invalidChar)
    }
}

class TextValidateNameAlreadyExistsException : TextException("Text name contains invalid char") {
    override fun getLocalizedMessage(stringProvider: TextStringProvider): String {
        return stringProvider.textValidateNameAlreadyExists
    }
}