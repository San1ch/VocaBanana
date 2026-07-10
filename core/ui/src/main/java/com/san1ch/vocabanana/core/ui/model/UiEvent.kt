package com.san1ch.vocabanana.core.ui.model

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class OpenUrl(val url: String) : UiEvent()
}