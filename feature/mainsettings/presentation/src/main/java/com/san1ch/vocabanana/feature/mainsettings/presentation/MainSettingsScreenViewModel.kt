package com.san1ch.vocabanana.feature.mainsettings.presentation

import android.app.PendingIntent
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.Logger
import com.san1ch.vocabanana.core.essentials.model.AppThemeMode
import com.san1ch.vocabanana.core.essentials.network.GoogleAuthManager
import com.san1ch.vocabanana.core.essentials.repositories.BackupSettingsRepository
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
import com.san1ch.vocabanana.core.essentials.resources.network.GoogleApiStringProvider
import com.san1ch.vocabanana.core.ui.BaseViewModel
import com.san1ch.vocabanana.core.ui.exception.DriveConsentRequiredException
import com.san1ch.vocabanana.core.ui.model.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSettingsScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val backupSettingsRepository: BackupSettingsRepository,
    private val settingsRouter: SettingsRouter,
    private val googleAuthManager: GoogleAuthManager,
    private val googleApiStringProvider: GoogleApiStringProvider,
    private val logger: Logger,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SettingsUiEvent>()
    val uiEvent: SharedFlow<SettingsUiEvent> = _uiEvent.asSharedFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        settingsRepository.themeFlow.onEach { theme ->
            _uiState.update { it.copy(currentTheme = theme) }
        }.launchIn(viewModelScope)

        backupSettingsRepository.localBackupPathFlow.onEach { rawPath ->
            val readablePath = if (rawPath.isNotBlank() && rawPath.startsWith("content://")) {
                formatUriToReadablePath(rawPath.toUri())
            } else {
                rawPath
            }

            _uiState.update {
                it.copy(
                    currentLocalPath = readablePath,
                    selectedLocalPath = readablePath,
                    rawLocalUri = rawPath,
                )
            }
        }.launchIn(viewModelScope)

        backupSettingsRepository.isLocalBackupEnabledFlow.onEach { enabled ->
            _uiState.update {
                it.copy(
                    isLocalBackupEnabled = enabled,
                    isLocalToggleEnabled = enabled,
                )
            }
        }.launchIn(viewModelScope)

        backupSettingsRepository.isCloudBackupEnabledFlow.onEach { enabled ->
            _uiState.update {
                it.copy(
                    isCloudBackupEnabled = enabled,
                    isCloudToggleEnabled = enabled,
                )
            }
        }.launchIn(viewModelScope)

        // Додаємо наглядання за email
        backupSettingsRepository.currentUserEmailFlow.onEach { email ->
            _uiState.update { it.copy(currentUserEmail = email) }
        }.launchIn(viewModelScope)
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ChangeTheme -> {
                viewModelScope.launch(Dispatchers.IO) {
                    settingsRepository.setTheme(intent.theme)
                }
            }

            SettingsIntent.BackClicked -> {
                settingsRouter.navigateBack()
            }

            is SettingsIntent.ToggleLocalBackup -> {
                _uiState.update { it.copy(isLocalToggleEnabled = intent.enabled) }
            }

            is SettingsIntent.ToggleCloudBackup -> {
                _uiState.update { it.copy(isCloudToggleEnabled = intent.enabled) }
            }

            is SettingsIntent.UpdateLocalPathUri -> {
                val rawUriString = intent.uri.toString()
                val readablePath = formatUriToReadablePath(intent.uri)
                _uiState.update {
                    it.copy(
                        selectedLocalPath = readablePath,
                        rawLocalUri = rawUriString,
                    )
                }
            }

            is SettingsIntent.ConnectGoogleDriveClicked -> {
                requestGoogleDriveAuth()
            }

            is SettingsIntent.GoogleAuthResolved -> {
                requestGoogleDriveAuth()
            }

            is SettingsIntent.SaveBackupSettings -> {
                if (uiState.value.isSaveValid) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val state = uiState.value

                        backupSettingsRepository.setLocalBackupEnabled(state.isLocalToggleEnabled)
                        if (state.isLocalToggleEnabled) {
                            backupSettingsRepository.setLocalBackupPath(state.rawLocalUri)
                        } else {
                            backupSettingsRepository.clearLocalBackupPath()
                        }
                    }
                }
            }
            is SettingsIntent.DisconnectGoogleDriveClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    googleAuthManager.revokeAccess().onSuccess {
                        backupSettingsRepository.setCloudBackupEnabled(false)
                        backupSettingsRepository.clearCurrentUserEmail()
                    }.onFailure { error ->
                    }
                }
            }
        }
    }
    private fun requestGoogleDriveAuth() {
        viewModelScope.launch {
            val result = googleAuthManager.requestAccount()

            result.onSuccess {
                backupSettingsRepository.setCloudBackupEnabled(true)
                backupSettingsRepository.setCurrentUserEmail(googleAuthManager.getUserEmail().getOrNull() ?: "")
                sendEvent(UiEvent.ShowToast(googleApiStringProvider.authSuccessMessage))
            }.onFailure { error ->
                when (error) {
                    is DriveConsentRequiredException -> {
                        _uiEvent.emit(SettingsUiEvent.LaunchGoogleConsent(error.pendingIntent))
                    }
                    else -> {
                        val errorMessage = error.message ?: return@onFailure
                        _uiState.update { it.copy(isCloudToggleEnabled = false) }
                        sendEvent(UiEvent.ShowToast(errorMessage))
                    }
                }
            }
        }
    }

    private fun formatUriToReadablePath(uri: Uri): String {
        val path = uri.path ?: return "Selected Folder"
        val cleanPath = path
            .replace("/tree/primary:", "")
            .replace("/document/primary:", "")
            .replace("primary:", "")
            .replace("%2F", "/")

        val segments = cleanPath.split("/").filter { it.isNotEmpty() }
        return if (segments.size >= 2) {
            "${segments[segments.size - 2]} / ${segments.last()}"
        } else if (segments.isNotEmpty()) {
            segments.last()
        } else {
            "Internal Storage"
        }
    }
}

data class SettingsUiState(
    val currentTheme: AppThemeMode = AppThemeMode.AUTO,

    val currentLocalPath: String = "",
    val selectedLocalPath: String = "",
    val rawLocalUri: String = "",

    val isLocalBackupEnabled: Boolean = false,
    val isLocalToggleEnabled: Boolean = false,

    val isCloudBackupEnabled: Boolean = false,
    val isCloudToggleEnabled: Boolean = false,

    val currentUserEmail: String? = null,
) {
    val hasUnsavedChanges: Boolean
        get() = (currentLocalPath != selectedLocalPath) ||
            (isLocalBackupEnabled != isLocalToggleEnabled)

    val isSaveValid: Boolean
        get() {
            val localValid = if (isLocalToggleEnabled) {
                rawLocalUri.isNotBlank() || currentLocalPath.isNotBlank()
            } else {
                true
            }

            val cloudValid = true

            return localValid && cloudValid
        }
}

sealed interface SettingsIntent {
    data class ChangeTheme(val theme: AppThemeMode) : SettingsIntent
    object BackClicked : SettingsIntent
    data class ToggleLocalBackup(val enabled: Boolean) : SettingsIntent
    data class ToggleCloudBackup(val enabled: Boolean) : SettingsIntent
    data class UpdateLocalPathUri(val uri: Uri) : SettingsIntent
    object ConnectGoogleDriveClicked : SettingsIntent
    object GoogleAuthResolved : SettingsIntent
    object SaveBackupSettings : SettingsIntent
    object DisconnectGoogleDriveClicked : SettingsIntent
}

sealed interface SettingsUiEvent {
    data class LaunchGoogleConsent(val pendingIntent: PendingIntent) : SettingsUiEvent
}
