package com.example.vocabanana.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabanana.android.DataStoreSettingsRepository
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.core.presentation.uistate.UiStateError
import com.example.vocabanana.core.presentation.uistate.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NavGraphViewModel @Inject constructor(
    dataStoreSettingsRepository: DataStoreSettingsRepository
) : ViewModel() {
    val startDestination: StateFlow<UiState<String>> =
        dataStoreSettingsRepository.initActiveFlow
            .map { isActive: Boolean ->
                if (isActive) UiState.Success(AppDestinations.INIT_DESTINATION)
                else UiState.Success(AppDestinations.MAIN_DESTINATION)
            }
            .catch { e ->
                UiState.Error(
                    UiStateError.Unknown(e.message ?: "Unknown error").toUiText()
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )
}