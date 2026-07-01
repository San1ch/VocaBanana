package com.san1ch.vocabanana.feature.init.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
import com.san1ch.vocabanana.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitScreenViewModel @Inject constructor(
    private val initRouter: InitRouter,
    private val settingsRepository: SettingsRepository
) : BaseViewModel() {
    fun finishInit() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.setInitActive(false)
        }
    }
}