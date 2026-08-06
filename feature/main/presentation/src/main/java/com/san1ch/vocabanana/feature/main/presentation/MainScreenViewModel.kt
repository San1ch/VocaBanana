package com.san1ch.vocabanana.feature.main.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.backup.CloudBackupManager
import com.san1ch.vocabanana.core.essentials.backup.LocalBackupManager
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
        backupSettingsRepository.isCloudBackupEnabledFlow
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
                _uiState.update { it.copy(isConfirmLocalBackupWindowOpen = true) }
            }

            MainUiIntent.CloseConfirmLocalBackupWindow -> {
                _uiState.update { it.copy(isConfirmLocalBackupWindowOpen = false) }
            }

            MainUiIntent.OpenConfirmCloudBackupWindow -> {
                _uiState.update { it.copy(isConfirmCloudBackupWindowOpen = true) }
            }

            MainUiIntent.CloseConfirmCloudBackupWindow -> {
                _uiState.update { it.copy(isConfirmCloudBackupWindowOpen = false) }
            }

            MainUiIntent.OpenConfirmLoadCloudBackupWindow -> {
                _uiState.update { it.copy(isConfirmLoadCloudBackupWindowOpen = true) }
            }

            MainUiIntent.CloseConfirmLoadCloudBackupWindow -> {
                _uiState.update { it.copy(isConfirmLoadCloudBackupWindowOpen = false) }
            }

            MainUiIntent.LocalBackup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    localBackupManager.backup()
                    backupSettingsRepository.setLastLocalBackupTime(System.currentTimeMillis())
                    onIntent(MainUiIntent.CloseConfirmLocalBackupWindow)
                    sendEvent(ShowToast(backupStringProvider.backupSuccessMessage()))
                }
            }

            is MainUiIntent.LoadLocalBackup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    localBackupManager.restore(intent.fileUriString)
                    sendEvent(ShowToast(backupStringProvider.loadSuccessMessage()))
                }
            }

            MainUiIntent.BackupCloud -> {
                viewModelScope.launch(Dispatchers.IO) {
                    cloudBackupManager.backup()
                    backupSettingsRepository.setLastCloudBackupTime(System.currentTimeMillis())
                    onIntent(MainUiIntent.CloseConfirmCloudBackupWindow)
                    sendEvent(ShowToast(backupStringProvider.backupSuccessMessage()))
                }
            }

            MainUiIntent.LoadCloudBackup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    cloudBackupManager.restore()
                    onIntent(MainUiIntent.CloseConfirmLoadCloudBackupWindow)
                    sendEvent(ShowToast(backupStringProvider.loadSuccessMessage()))
                }
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
}
