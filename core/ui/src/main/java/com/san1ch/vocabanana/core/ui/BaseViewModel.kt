package com.san1ch.vocabanana.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.ui.model.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}