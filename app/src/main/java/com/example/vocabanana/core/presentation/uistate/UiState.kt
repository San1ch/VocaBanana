package com.example.vocabanana.core.presentation.uistate

import com.example.vocabanana.R
import com.example.vocabanana.core.data.AppError
import com.example.vocabanana.core.presentation.UiText

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T : Any>(val data: T) : UiState<T>
    data class Error(val message: UiText) : UiState<Nothing>
}

sealed class UiStateError : AppError {
    object Unknown : UiStateError()
}


fun UiStateError.toUiText(): UiText {
    return when (this) {
        is UiStateError.Unknown -> UiText.StringResource(R.string.error_unknown)
    }
}