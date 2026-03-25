package com.example.vocabanana.feature.word.data

sealed class ValidateResult<out V, out E> {
    data class Success<V>(val value: V) : ValidateResult<V, Nothing>()
    data class Error<E>(val error: E) : ValidateResult<Nothing, E>()
}

fun <T, R, E> ValidateResult<T, E>.map(transform: (T) -> R): ValidateResult<R, E> = when (this) {
    is ValidateResult.Success -> ValidateResult.Success(transform(value))
    is ValidateResult.Error -> this
}