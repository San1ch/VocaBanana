package com.example.vocabanana.feature.init

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.android.DataStoreSettingsRepository
import com.example.vocabanana.core.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitScreenViewModel @Inject constructor(
    private val dataStoreSettingsRepository: DataStoreSettingsRepository
) : BaseViewModel() {
    fun finishInit() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreSettingsRepository.setInitActive(false)
        }
    }
}