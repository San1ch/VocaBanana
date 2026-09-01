package com.san1ch.vocabanana.core.essentials.model

sealed interface ResultWithState<out R, out L> {
    // Represents successful result with data
    data class Success<R>(val data: R) : ResultWithState<R, Nothing>

    // Represents loading state with custom progress data
    data class Loading<L>(val loadingState: L) : ResultWithState<Nothing, L>

    // Represents failure with an exception
    data class Error(val exception: Throwable) : ResultWithState<Nothing, Nothing>
}

inline fun <R, L> ResultWithState<R, L>.fold(
    onSuccess: (R) -> Any,
    onLoading: (L) -> Any = {},
    onError: (Throwable) -> Any,
): Any = when (this) {
    is ResultWithState.Success -> onSuccess(data)
    is ResultWithState.Loading -> onLoading(loadingState)
    is ResultWithState.Error -> onError(exception)
}

fun <R, L> ResultWithState<R, L>.onSuccess(onSuccess: (R) -> Unit) = when (this) {
    is ResultWithState.Success -> onSuccess(data)
    else -> Unit
}

fun <R, L> ResultWithState<R, L>.onLoading(onLoading: (L) -> Unit) = when (this) {
    is ResultWithState.Loading -> onLoading(loadingState)
    else -> Unit
}

fun <R, L> ResultWithState<R, L>.onError(onError: (Throwable) -> Unit) = when (this) {
    is ResultWithState.Error -> onError(exception)
    else -> Unit
}
