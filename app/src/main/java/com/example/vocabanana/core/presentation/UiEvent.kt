package com.example.vocabanana.core.presentation

sealed class UiEvent {
    data class ShowToast(val message: UiText) : UiEvent()
}