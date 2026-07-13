package com.san1ch.vocabanana.core.ui.state

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
