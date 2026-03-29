package com.example.vocabanana.feature.text.presentation

import com.example.vocabanana.R
import com.example.vocabanana.core.presentation.UiText
import com.example.vocabanana.feature.text.data.TextValidateError

fun TextValidateError.toUiText(): UiText {
    return when (this) {
        TextValidateError.EmptyText -> UiText.StringResource(R.string.text_validate_error_empty)

        is TextValidateError.InvalidName -> UiText.StringResource(
            R.string.text_validate_error_invalid_name,
            this.invalidChar
        )

        TextValidateError.NameAlreadyExists -> UiText.StringResource(R.string.text_validate_error_name_already_exists)

        is TextValidateError.TooLongName -> UiText.StringResource(
            R.string.text_validate_error_too_long_name,
            this.invalidLength
        )
    }
}