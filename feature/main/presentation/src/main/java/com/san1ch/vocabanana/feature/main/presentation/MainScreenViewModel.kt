package com.san1ch.vocabanana.feature.main.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.IsCloudBackupEnabledUseCase
import com.san1ch.vocabanana.core.essentials.backup.CloudBackupManager
import com.san1ch.vocabanana.core.essentials.backup.LocalBackupManager
import com.san1ch.vocabanana.core.essentials.model.ResultWithState
import com.san1ch.vocabanana.core.essentials.model.fold
import com.san1ch.vocabanana.core.essentials.repositories.BackupSettingsRepository
import com.san1ch.vocabanana.core.essentials.resources.AppStringProvider
import com.san1ch.vocabanana.core.essentials.resources.featureproviders.BackupStringProvider
import com.san1ch.vocabanana.core.ui.BaseViewModel
import com.san1ch.vocabanana.core.ui.model.UiEvent.ShowToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val backupSettingsRepository: BackupSettingsRepository,
    private val isCloudBackupEnabled: IsCloudBackupEnabledUseCase,
    private val localBackupManager: LocalBackupManager,
    private val cloudBackupManager: CloudBackupManager,
    private val router: MainRouter,
    private val appStringProvider: AppStringProvider,
    private val backupStringProvider: BackupStringProvider,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(
        MainUiState(
            appName = appStringProvider.appName,
        ),
    )
    val uiState = _uiState.asStateFlow()

    init {
        observeBackupSettings()
    }

    private fun observeBackupSettings() {
        // Observe local backup status
        backupSettingsRepository.isLocalBackupEnabledFlow
            .onEach { enabled ->
                _uiState.update { it.copy(isLocalBackupEnabled = enabled) }
            }
            .launchIn(viewModelScope)

        backupSettingsRepository.isLocalBackupNeedUpdateFlow
            .onEach { needUpdate ->
                _uiState.update { it.copy(isLocalBackupNeedUpdate = needUpdate) }
            }
            .launchIn(viewModelScope)

        // Observe cloud backup status
        isCloudBackupEnabled()
            .onEach { enabled ->
                _uiState.update { it.copy(isCloudBackupEnabled = enabled) }
            }
            .launchIn(viewModelScope)

        backupSettingsRepository.isCloudBackupNeedUpdateFlow
            .onEach { needUpdate ->
                _uiState.update { it.copy(isCloudBackupNeedUpdate = needUpdate) }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: MainUiIntent) {
        when (intent) {
            MainUiIntent.NavigateToVocabulary -> router.navigateToVocabulary()
            MainUiIntent.NavigateToTexts -> router.navigateToTextList()
            MainUiIntent.NavigateToSettings -> router.navigateToMainSettings()
            MainUiIntent.NavigateToDebug -> router.navigateToDebug()

            MainUiIntent.OpenConfirmLocalBackupWindow -> {
                _uiState.update { it.copy(confirmLocalBackupWindowState = true) }
            }

            MainUiIntent.CloseConfirmLocalBackupWindow -> {
                _uiState.update { it.copy(confirmLocalBackupWindowState = false) }
            }

            MainUiIntent.OpenConfirmCloudBackupWindow -> {
                _uiState.update { it.copy(confirmCloudBackupWindowState = true) }
            }

            MainUiIntent.CloseConfirmCloudBackupWindow -> {
                _uiState.update { it.copy(confirmCloudBackupWindowState = false) }
            }

            MainUiIntent.OpenConfirmLoadCloudBackupWindow -> {
                _uiState.update { it.copy(confirmLoadCloudBackupWindowState = true) }
            }

            MainUiIntent.CloseConfirmLoadCloudBackupWindow -> {
                _uiState.update { it.copy(confirmLoadCloudBackupWindowState = false) }
            }

            MainUiIntent.LocalBackup -> {
                executeBackupAction(
                    closeIntent = MainUiIntent.CloseConfirmLocalBackupWindow,
                    updateState = { state, res -> state.copy(localBackupResult = res) },
                    action = { localBackupManager.backup() },
                    onSuccess = {
                        viewModelScope.launch(Dispatchers.IO) {
                            backupSettingsRepository.setLastLocalBackupTime(System.currentTimeMillis())
                            sendEvent(ShowToast(backupStringProvider.backupSuccessMessage()))
                            _uiState.update { it.copy(localBackupResult = null) }
                        }
                    },
                )
            }

            MainUiIntent.BackupCloud -> {
                executeBackupAction(
                    closeIntent = MainUiIntent.CloseConfirmCloudBackupWindow,
                    updateState = { state, res -> state.copy(cloudBackupResult = res) },
                    action = { cloudBackupManager.backup() },
                    onSuccess = {
                        viewModelScope.launch(Dispatchers.IO) {
                            backupSettingsRepository.setLastCloudBackupTime(System.currentTimeMillis())
                            sendEvent(ShowToast(backupStringProvider.backupSuccessMessage()))
                            _uiState.update { it.copy(cloudBackupResult = null) }
                        }
                    },
                )
            }

            is MainUiIntent.LoadLocalBackup -> {
                executeBackupAction(
                    updateState = { state, _ -> state },
                    action = { localBackupManager.restore(intent.fileUriString) },
                    onSuccess = {
                        sendEvent(ShowToast(backupStringProvider.loadSuccessMessage()))
                    },
                )
            }

            MainUiIntent.LoadCloudBackup -> {
                executeBackupAction(
                    closeIntent = MainUiIntent.CloseConfirmLoadCloudBackupWindow,
                    updateState = { state, res -> state.copy(loadCloudBackupResult = res) },
                    action = { cloudBackupManager.restore() },
                    onSuccess = {
                        sendEvent(ShowToast(backupStringProvider.loadSuccessMessage()))
                        _uiState.update { it.copy(loadCloudBackupResult = null) }
                    },
                )
            }

            MainUiIntent.ShowLastBackupTime -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val lastBackupTime = backupSettingsRepository.lastLocalBackupTimeFlow.first()
                    sendEvent(
                        ShowToast(
                            backupStringProvider.lastBackupTimeMessage(lastBackupTime),
                        ),
                    )
                }
            }

            MainUiIntent.ShowLastCloudBackupTime -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val lastBackupTime = backupSettingsRepository.lastCloudBackupTimeFlow.first()
                    sendEvent(
                        ShowToast(
                            backupStringProvider.lastBackupTimeMessage(lastBackupTime),
                        ),
                    )
                }
            }
        }
    }

    private fun executeBackupAction(
        closeIntent: MainUiIntent? = null,
        updateState: (MainUiState, ResultWithState<Unit, Unit>) -> MainUiState,
        action: suspend () -> ResultWithState<Unit, Unit>,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { updateState(it, ResultWithState.Loading(Unit)) }

            try {
                val result = action()

                _uiState.update { updateState(it, result) }

                result.fold(
                    onSuccess = {
                        onSuccess()
                    },
                    onError = { exception ->
                        sendEvent(ShowToast(exception.message ?: "Unknown error"))
                    },
                )
            } catch (e: Throwable) {
                _uiState.update { updateState(it, ResultWithState.Error(e)) }
                sendEvent(ShowToast(e.message ?: "Unknown error"))
            } finally {
                closeIntent?.let { intent ->
                    onIntent(intent)
                }
            }
        }
    }
}
