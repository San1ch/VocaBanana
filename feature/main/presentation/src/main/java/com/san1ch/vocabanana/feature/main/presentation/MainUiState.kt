package com.san1ch.vocabanana.feature.main.presentation

data class MainUiState(
    val appName: String,
    val isLocalBackupEnabled: Boolean = false,
    val isBackupNeedUpdate: Boolean = false,
    val isConfirmBackupWindowOpen: Boolean = false,

)
