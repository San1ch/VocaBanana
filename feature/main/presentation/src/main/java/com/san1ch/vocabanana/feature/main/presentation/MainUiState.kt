package com.san1ch.vocabanana.feature.main.presentation

data class MainUiState(
    val appName: String,

    val isLocalBackupEnabled: Boolean = false,
    val isLocalBackupNeedUpdate: Boolean = false,

    val isCloudBackupEnabled: Boolean = false,
    val isCloudBackupNeedUpdate: Boolean = false,

    val isConfirmLocalBackupWindowOpen: Boolean = false,
    val isConfirmCloudBackupWindowOpen: Boolean = false,
    val isConfirmLoadCloudBackupWindowOpen: Boolean = false,
)
