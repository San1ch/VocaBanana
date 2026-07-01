package com.san1ch.vocabanana.core.essentials.model

import com.san1ch.vocabanana.core.essentials.exceptions.AppException


sealed class ValidateResult<out V> {
    data class Success<V>(val value: V) : ValidateResult<V>()
    data class Error(val error: AppException) : ValidateResult<Nothing>()
}

inline fun <V, R> ValidateResult<V>.map(transform: (V) -> R): ValidateResult<R> =
    when (this) {
        is ValidateResult.Success -> ValidateResult.Success(transform(value))
        is ValidateResult.Error -> this
    }

fun <V, R> ValidateResult<V>.fold(
    onSuccess: (V) -> R,
    onError: (AppException) -> R
): R = when (this) {
    is ValidateResult.Success -> onSuccess(value)
    is ValidateResult.Error -> onError(error)
}



