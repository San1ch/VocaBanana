package com.example.vocabanana.feature.text.presentation

import com.example.vocabanana.R
import com.example.vocabanana.core.presentation.UiText
import com.example.vocabanana.feature.text.data.TextConstant.MAX_NAME_LENGTH
import com.example.vocabanana.feature.text.data.TextValidateError

fun TextValidateError.toUiText(): UiText = when (this) {
    TextValidateError.EmptyText -> UiText(R.string.text_validate_error_empty, emptyList())

    is TextValidateError.InvalidName ->
        UiText(
            R.string.text_validate_error_invalid_name,
            listOf(invalidChar)
        )

    is TextValidateError.TooLongName ->
        UiText(
            R.string.text_validate_error_too_long_name,
            listOf(MAX_NAME_LENGTH)
        )

    TextValidateError.NameAlreadyExists -> UiText(R.string.text_validate_error_name_already_exists, emptyList())
}