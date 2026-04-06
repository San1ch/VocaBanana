package com.example.vocabanana.core.domain.model

sealed class ValidateResult<out V, out E> {
    data class Success<V>(val value: V) : ValidateResult<V, Nothing>()
    data class Error<E>(val error: E) : ValidateResult<Nothing, E>()
}

inline fun <V, R, E> ValidateResult<V, E>.map(transform: (V) -> R): ValidateResult<R, E> =
    when (this) {
        is ValidateResult.Success -> ValidateResult.Success(transform(value))
        is ValidateResult.Error -> this
    }

fun <V, E, R> ValidateResult<V, E>.fold(
    onSuccess: (V) -> R,
    onError: (E) -> R
): R = when (this) {
    is ValidateResult.Success -> onSuccess(value)
    is ValidateResult.Error -> onError(error)
}



