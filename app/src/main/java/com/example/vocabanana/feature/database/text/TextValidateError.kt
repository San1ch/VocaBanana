package com.example.vocabanana.feature.database.text

import com.example.vocabanana.core.domain.model.ValidationError


sealed class TextValidateError : ValidationError {
    object EmptyText : TextValidateError()
    data class TooLongName(val invalidLength: Int) : TextValidateError()
    data class InvalidName(val invalidChar: Char) : TextValidateError()
    object NameAlreadyExists : TextValidateError()
}

