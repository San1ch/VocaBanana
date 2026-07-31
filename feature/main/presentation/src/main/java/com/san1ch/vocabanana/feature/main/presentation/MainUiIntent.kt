package com.san1ch.vocabanana.feature.main.presentation

sealed class MainUiIntent {
    object NavigateToVocabulary : MainUiIntent()
    object NavigateToTexts : MainUiIntent()
    object NavigateToSettings : MainUiIntent()
    object NavigateToDebug : MainUiIntent()
    object OpenConfirmBackupWindow : MainUiIntent()
    object CloseConfirmBackupWindow : MainUiIntent()
    object Backup : MainUiIntent()
    data class LoadBackup(val fileUriString: String) : MainUiIntent()
    object ShowLastBackupTime : MainUiIntent()
}
