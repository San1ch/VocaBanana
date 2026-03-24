package com.example.vocabanana.feature.word.data

sealed class ValidateResult<out V, out T> {
    data class Success<V>(val value: V) : ValidateResult<V, Nothing>()
    data class Error<T>(val error: T) : ValidateResult<Nothing, T>()
}