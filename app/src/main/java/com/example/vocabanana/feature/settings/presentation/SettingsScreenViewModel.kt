package com.example.vocabanana.feature.settings.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.io.preference.SettingsRepository
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val repository: SettingsRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ChangeTheme -> {
                _uiState.update { it.copy(currentTheme = intent.theme) }
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
                sendEvent(UiEvent.NavigateBack)
            }
        }
    }
}