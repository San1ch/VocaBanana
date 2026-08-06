package com.san1ch.vocabanana.feature.mainsettings.presentation

import android.app.PendingIntent
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.Logger
import com.san1ch.vocabanana.core.essentials.backup.GoogleDriveTokenProvider
import com.san1ch.vocabanana.core.essentials.model.AppThemeMode
import com.san1ch.vocabanana.core.essentials.repositories.BackupSettingsRepository
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
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
    private val tokenManager: GoogleDriveTokenProvider,
    private val logger: Logger,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SettingsUiEvent>()
    val uiEvent: SharedFlow<SettingsUiEvent> = _uiEvent.asSharedFlow()

    init {
        settingsRepository.themeFlow.onEach { theme ->
            _uiState.update { it.copy(currentTheme = theme) }
        }.launchIn(viewModelScope)

        backupSettingsRepository.localBackupPathFlow.onEach { rawPath ->
            val readablePath = if (rawPath.isNotBlank() && rawPath.startsWith("content://")) {
                formatUriToReadablePath(Uri.parse(rawPath))
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
                    currentIsLocalEnabled = enabled,
                    isLocalEnabled = enabled,
                )
            }
        }.launchIn(viewModelScope)

        backupSettingsRepository.cloudEmailFlow.onEach { email ->
            _uiState.update {
                it.copy(
                    currentCloudEmail = email,
                    selectedCloudEmail = email,
                )
            }
        }.launchIn(viewModelScope)

        backupSettingsRepository.isCloudBackupEnabledFlow.onEach { enabled ->
            _uiState.update {
                it.copy(
                    currentIsCloudEnabled = enabled,
                    isCloudEnabled = enabled,
                )
            }
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
                _uiState.update { it.copy(isLocalEnabled = intent.enabled) }
            }

            is SettingsIntent.ToggleCloudBackup -> {
                _uiState.update { it.copy(isCloudEnabled = intent.enabled) }
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

            is SettingsIntent.UpdateCloudEmail -> {
                _uiState.update { it.copy(selectedCloudEmail = intent.email) }
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

                        backupSettingsRepository.setLocalBackupEnabled(state.isLocalEnabled)
                        if (state.isLocalEnabled) {
                            backupSettingsRepository.setLocalBackupPath(state.rawLocalUri)
                        } else {
                            backupSettingsRepository.setLocalBackupPath("")
                        }

                        backupSettingsRepository.setCloudBackupEnabled(state.isCloudEnabled)
                        if (state.isCloudEnabled) {
                            backupSettingsRepository.setCloudEmail(state.selectedCloudEmail)
                        } else {
                            backupSettingsRepository.setCloudEmail("")
                        }
                    }
                }
            }
        }
    }

    private fun requestGoogleDriveAuth() {
        viewModelScope.launch {
            val result = tokenManager.obtainAccessToken()
            result.onSuccess { token ->
                _uiState.update { it.copy(selectedCloudEmail = "Connected Google Account") }
            }.onFailure { error ->
                when (val error = error) {
                    is DriveConsentRequiredException -> {
                        _uiEvent.emit(SettingsUiEvent.LaunchGoogleConsent(error.pendingIntent))
                    }

                    else -> {
                        val errorMessage = error.message ?: return@onFailure
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

    val currentIsLocalEnabled: Boolean = false,
    val isLocalEnabled: Boolean = false,

    val currentCloudEmail: String = "",
    val selectedCloudEmail: String = "",
    val currentIsCloudEnabled: Boolean = false,
    val isCloudEnabled: Boolean = false,
) {
    val hasUnsavedChanges: Boolean
        get() = (currentLocalPath != selectedLocalPath) ||
            (currentCloudEmail != selectedCloudEmail) ||
            (currentIsLocalEnabled != isLocalEnabled) ||
            (currentIsCloudEnabled != isCloudEnabled)

    val isSaveValid: Boolean
        get() {
            val localValid = if (isLocalEnabled) {
                rawLocalUri.isNotBlank() || currentLocalPath.isNotBlank()
            } else {
                true
            }

            val cloudValid = if (isCloudEnabled) {
                selectedCloudEmail.isNotBlank()
            } else {
                true
            }

            return localValid && cloudValid
        }
}

sealed interface SettingsIntent {
    data class ChangeTheme(val theme: AppThemeMode) : SettingsIntent
    object BackClicked : SettingsIntent
    data class ToggleLocalBackup(val enabled: Boolean) : SettingsIntent
    data class ToggleCloudBackup(val enabled: Boolean) : SettingsIntent
    data class UpdateLocalPathUri(val uri: Uri) : SettingsIntent
    data class UpdateCloudEmail(val email: String) : SettingsIntent
    object ConnectGoogleDriveClicked : SettingsIntent
    object GoogleAuthResolved : SettingsIntent
    object SaveBackupSettings : SettingsIntent
}

sealed interface SettingsUiEvent {
    data class LaunchGoogleConsent(val pendingIntent: PendingIntent) : SettingsUiEvent
}
