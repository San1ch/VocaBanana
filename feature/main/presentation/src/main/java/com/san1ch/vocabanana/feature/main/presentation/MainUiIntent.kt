package com.san1ch.vocabanana.feature.main.presentation

sealed class MainUiIntent {
    object NavigateToVocabulary : MainUiIntent()
    object NavigateToTexts : MainUiIntent()
    object NavigateToSettings : MainUiIntent()
    object NavigateToDebug : MainUiIntent()
}
