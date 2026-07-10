package com.san1ch.vocabanana.core.ui.state

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data object Empty : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val error: UiStateError) : UiState<Nothing>
}

sealed class UiStateError(open val message: String) {
    data class Name(override val message: String) : UiStateError(message)
    data class Content(override val message: String) : UiStateError(message)
    data class Unknown(override val message: String) : UiStateError(message)
}