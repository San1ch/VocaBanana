package com.san1ch.vocabanana.feature.main.presentation

sealed class MainUiIntent {
    object NavigateToVocabulary : MainUiIntent()
    object NavigateToTexts : MainUiIntent()
    object NavigateToSettings : MainUiIntent()
    object NavigateToDebug : MainUiIntent()
    object OpenConfirmLocalBackupWindow : MainUiIntent()
    object CloseConfirmLocalBackupWindow : MainUiIntent()
    object LocalBackup : MainUiIntent()
    data class LoadLocalBackup(val fileUriString: String) : MainUiIntent()
    object ShowLastBackupTime : MainUiIntent()

    // Cloud backup intents
    object OpenConfirmCloudBackupWindow : MainUiIntent()
    object CloseConfirmCloudBackupWindow : MainUiIntent()
    object OpenConfirmLoadCloudBackupWindow : MainUiIntent()
    object CloseConfirmLoadCloudBackupWindow : MainUiIntent()
    object BackupCloud : MainUiIntent()
    object LoadCloudBackup : MainUiIntent()
    object ShowLastCloudBackupTime : MainUiIntent()
}
