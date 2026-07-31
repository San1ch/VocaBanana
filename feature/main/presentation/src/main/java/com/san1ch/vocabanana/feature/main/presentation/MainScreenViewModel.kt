package com.san1ch.vocabanana.feature.main.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.backup.LocalBackupManager
import com.san1ch.vocabanana.core.essentials.repositories.BackupSettingsRepository
import com.san1ch.vocabanana.core.essentials.resources.AppStringProvider
import com.san1ch.vocabanana.core.essentials.resources.BackupStringProvider
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
    private val localBackupManager: LocalBackupManager,
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
        observeCurrentBackupMode()
    }

    private fun observeCurrentBackupMode() {
        backupSettingsRepository.isLocalBackupEnabledFlow
            .onEach { enabled ->
                _uiState.update { it.copy(isLocalBackupEnabled = enabled) }
            }
            .launchIn(viewModelScope)

        backupSettingsRepository.isBackupNeedUpdateFlow
            .onEach { needUpdate ->
                _uiState.update { it.copy(isBackupNeedUpdate = needUpdate) }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: MainUiIntent) {
        when (intent) {
            MainUiIntent.NavigateToVocabulary -> router.navigateToVocabulary()
            MainUiIntent.NavigateToTexts -> router.navigateToTextList()
            MainUiIntent.NavigateToSettings -> router.navigateToMainSettings()
            MainUiIntent.NavigateToDebug -> router.navigateToDebug()

            MainUiIntent.OpenConfirmBackupWindow -> {
                _uiState.update { it.copy(isConfirmBackupWindowOpen = true) }
            }

            MainUiIntent.Backup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    localBackupManager.backup()
                    backupSettingsRepository.setLastBackupTime(System.currentTimeMillis())
                    onIntent(MainUiIntent.CloseConfirmBackupWindow)
                    sendEvent(ShowToast(backupStringProvider.backupSuccessMessage()))
                }
            }

            is MainUiIntent.LoadBackup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    localBackupManager.restore(intent.fileUriString)
                    sendEvent(ShowToast(backupStringProvider.loadSuccessMessage()))
                }
            }

            MainUiIntent.CloseConfirmBackupWindow -> {
                _uiState.update { it.copy(isConfirmBackupWindowOpen = false) }
            }

            MainUiIntent.ShowLastBackupTime -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val lastBackupTime = backupSettingsRepository.lastBackupTimeFlow.first()
                    sendEvent(
                        ShowToast(
                            backupStringProvider.lastBackupTimeMessage(lastBackupTime),
                        ),
                    )
                }
            }
        }
    }
}
