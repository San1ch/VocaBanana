package com.example.vocabanana.feature.text.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabanana.feature.text.domain.usecase.SaveTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTextScreenViewModel @Inject constructor(
    private val saveTextUseCase: SaveTextUseCase
) : ViewModel() {

    fun processText(title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            saveTextUseCase(title, content)
        }
    }

}