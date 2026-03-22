package com.example.vocabanana.core.data.word

sealed class ValidateResult<out V, out T> {
    data class Success<V>(val value: V) : ValidateResult<V, Nothing>()
    data class Error<T>(val error: T) : ValidateResult<Nothing, T>()
}