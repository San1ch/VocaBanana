package com.example.vocabanana.core.presentation

import com.example.vocabanana.core.navigation.AppDestination

sealed class UiEvent {
    data class ShowToast(val uiText: UiText) : UiEvent()
    object NavigateBack : UiEvent()
    data class NavigateTo(val destination: AppDestination) : UiEvent()
}