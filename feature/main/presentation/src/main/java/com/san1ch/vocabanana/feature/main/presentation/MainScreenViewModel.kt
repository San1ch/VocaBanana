package com.san1ch.vocabanana.feature.main.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.database.repositories.SettingsRepository
import com.san1ch.vocabanana.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val router: MainRouter
) : BaseViewModel() {
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
