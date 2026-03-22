package com.example.vocabanana.feature.init

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.preference.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitScreenViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    fun finishInit() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsDataStore.setInitActive(false)
        }
    }
}