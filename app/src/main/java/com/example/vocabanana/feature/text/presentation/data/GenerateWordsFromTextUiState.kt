package com.example.vocabanana.feature.text.presentation.data

import com.example.vocabanana.core.presentation.UiText

sealed class GenerateWordsFromTextUiState {
    data class Loading(val message: UiText) : GenerateWordsFromTextUiState()
    data class Success(val words: List<WordUi>, val message: UiText) : GenerateWordsFromTextUiState()
    data class AllExist(val message: UiText) : GenerateWordsFromTextUiState()
    data class Error(val message: UiText) : GenerateWordsFromTextUiState()
    data class PartialSuccess(val message: UiText) : GenerateWordsFromTextUiState()
}