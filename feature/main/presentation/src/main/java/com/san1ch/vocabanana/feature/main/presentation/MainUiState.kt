package com.san1ch.vocabanana.feature.main.presentation

import com.san1ch.vocabanana.core.essentials.model.ResultWithState

data class MainUiState(
    val appName: String,

    val isLocalBackupEnabled: Boolean = false,
    val isLocalBackupNeedUpdate: Boolean = false,

    val isCloudBackupEnabled: Boolean = false,
    val isCloudBackupNeedUpdate: Boolean = false,

    val confirmLocalBackupWindowState: Boolean = false,
    val localBackupResult: ResultWithState<Unit, Unit>? = null,

    val confirmCloudBackupWindowState: Boolean = false,
    val cloudBackupResult: ResultWithState<Unit, Unit>? = null,

    val confirmLoadCloudBackupWindowState: Boolean = false,
    val loadCloudBackupResult: ResultWithState<Unit, Unit>? = null,
)
