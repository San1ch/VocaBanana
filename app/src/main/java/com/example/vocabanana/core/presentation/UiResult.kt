package com.example.vocabanana.core.presentation

sealed class UiResult {
    sealed class Success : UiResult(){
        object OK : Success()
        data class WithMessage(val message: UiText) : Success()
    }
    data class Error(val error: UiText) : UiResult()
}