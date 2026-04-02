package com.example.vocabanana.core.database.text

import com.example.vocabanana.core.data.ValidationError


sealed class TextValidateError : ValidationError {
    object EmptyText : TextValidateError()
    data class TooLongName(val invalidLength: Int) : TextValidateError()
    data class InvalidName(val invalidChar: Char) : TextValidateError()
    object NameAlreadyExists : TextValidateError()
}

