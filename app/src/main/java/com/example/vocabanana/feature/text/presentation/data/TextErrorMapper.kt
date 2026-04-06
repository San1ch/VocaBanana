package com.example.vocabanana.feature.text.presentation.data

import com.example.vocabanana.R
import com.example.vocabanana.core.presentation.UiText
import com.example.vocabanana.feature.database.text.TextValidateError
import com.example.vocabanana.feature.text.domain.GenerateWordsFromTextResult

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

fun GenerateWordsFromTextResult.Error.toUiText() = when(this){
    GenerateWordsFromTextResult.Error.NetworkError -> UiText.StringResource(R.string.network_error)
    GenerateWordsFromTextResult.Error.InvalidApiKey -> UiText.StringResource(R.string.API_invalid_api_key)
    GenerateWordsFromTextResult.Error.RateLimitExceeded -> UiText.StringResource(R.string.API_rate_limit_exceeded)
    GenerateWordsFromTextResult.Error.ServerError -> UiText.StringResource(R.string.server_error)
    is GenerateWordsFromTextResult.Error.Unknown -> UiText.StringResource(R.string.unknown_error, this.message)

}