package com.example.vocabanana.feature.main.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.android.DataStoreSettingsRepository
import com.example.vocabanana.core.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val dataStoreSettingsRepository: DataStoreSettingsRepository
) : BaseViewModel() {
    fun reloadInit() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreSettingsRepository.setInitActive(true)
        }
    }


}