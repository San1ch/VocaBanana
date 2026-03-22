package com.example.vocabanana.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.preference.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NavGraphViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
): ViewModel(){
    val startDestination = settingsDataStore.initActiveFlow
        .map { isActive: Boolean ->
            if (isActive) AppDestinations.INIT_DESTINATION
            else AppDestinations.MAIN_DESTINATION
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppDestinations.MAIN_DESTINATION
        )
}