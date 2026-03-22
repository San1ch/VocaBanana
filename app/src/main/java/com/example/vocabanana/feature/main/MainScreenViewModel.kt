package com.example.vocabanana.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.preference.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    fun reloadInit() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsDataStore.setInitActive(true)
        }
    }
}