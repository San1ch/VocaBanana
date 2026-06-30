package com.san1ch.vocabanana.core.ui

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class OpenUrl(val url: String) : UiEvent()
}