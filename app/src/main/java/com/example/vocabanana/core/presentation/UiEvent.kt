package com.example.vocabanana.core.presentation

sealed class UiEvent {
    data class ShowToast(val uiText: UiText) : UiEvent()
}