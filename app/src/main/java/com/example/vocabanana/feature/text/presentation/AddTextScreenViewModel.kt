package com.example.vocabanana.feature.text.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.presentation.UiEvent
import com.example.vocabanana.feature.text.domain.usecase.AddTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTextScreenViewModel @Inject constructor(
    private val addTextUseCase: AddTextUseCase
) : ViewModel() {

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    fun addText(title: String, content: String) {
        viewModelScope.launch {
            val error = addTextUseCase(title, content)

            if (error != null) {
                _events.emit(
                    UiEvent.ShowToast(error.toUiText())
                )
            }
        }
    }

}