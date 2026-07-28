package com.san1ch.vocabanana.core.ui.state

import androidx.compose.runtime.Composable

sealed interface Resource<out T> {
    data object Loading : Resource<Nothing>
    data object Empty : Resource<Nothing>
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val error: ResourceError) : Resource<Nothing>
}

sealed class ResourceError(open val message: String) {
    data class Name(override val message: String) : ResourceError(message)
    data class Content(override val message: String) : ResourceError(message)
    data class Unknown(override val message: String) : ResourceError(message)
}

fun <T, R> Resource<T>.fold(
    onLoading: () -> R,
    onEmpty: () -> R,
    onSuccess: (T) -> R,
    onError: (ResourceError) -> R,
): R = when (this) {
    is Resource.Loading -> onLoading()
    is Resource.Empty -> onEmpty()
    is Resource.Success -> onSuccess(data)
    is Resource.Error -> onError(error)
}

inline fun <T, R> Resource<T>.getOrNull(
    transform: (T) -> R,
): R? = when (this) {
    Resource.Empty -> null
    Resource.Loading -> null
    is Resource.Success -> transform(data)
    is Resource.Error -> null
}
