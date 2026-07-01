package com.san1ch.vocabanana.feature.main.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
import com.san1ch.vocabanana.core.essentials.resources.AppStringProvider
import com.san1ch.vocabanana.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val router: MainRouter,
    private val appStringProvider: AppStringProvider
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(MainUiState(appName = appStringProvider.appName))
    val uiState = _uiState.asStateFlow()

    fun onIntent(intent: MainUiIntent) {
        when (intent) {
            MainUiIntent.NavigateToVocabulary -> {
                router.launchVocabulary()
            }
            MainUiIntent.NavigateToTexts -> {
                router.launchTextList()
            }
            MainUiIntent.NavigateToSettings -> {
                router.launchMainSettings()
            }
            MainUiIntent.NavigateToDebug -> {
                router.launchDebug()
            }
        }
    }

    fun reloadInit() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.setInitActive(true)
        }
    }
}

