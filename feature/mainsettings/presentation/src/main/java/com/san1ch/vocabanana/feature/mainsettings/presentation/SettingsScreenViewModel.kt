package com.san1ch.vocabanana.feature.mainsettings.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.model.AppThemeMode
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
import com.san1ch.vocabanana.core.ui.BaseViewModel
import com.san1ch.vocabanana.core.ui.model.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val settingsRouter: SettingsRouter
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()


    init {
        repository.themeFlow.onEach { theme ->
            _uiState.update { it.copy(currentTheme = theme) }
        }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ChangeTheme -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.setTheme(intent.theme)
                }
            }

            is SettingsIntent.ToggleNotifications -> {
                _uiState.update { it.copy(isNotificationsEnabled = intent.enabled) }
            }

            is SettingsIntent.ChangeVolume -> {
                _uiState.update { it.copy(volume = intent.volume) }
            }

            is SettingsIntent.ChangeName -> {
                _uiState.update { it.copy(userName = intent.name) }
            }

            SettingsIntent.BackClicked -> {
                settingsRouter.navigateBack()
            }
        }
    }
}

sealed interface SettingsIntent {
    data class ChangeTheme(val theme: AppThemeMode) : SettingsIntent
    data class ToggleNotifications(val enabled: Boolean) : SettingsIntent
    data class ChangeVolume(val volume: Float) : SettingsIntent
    data class ChangeName(val name: String) : SettingsIntent
    data object BackClicked : SettingsIntent
}

data class SettingsUiState(
    val currentTheme: AppThemeMode = AppThemeMode.AUTO,
    val isNotificationsEnabled: Boolean = false,
    val volume: Float = 0.5f,
    val userName: String = ""
)