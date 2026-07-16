package com.san1ch.vocabanana.feature.text.presentation.textlist.handler

import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TextListStateController(
    private val state: MutableStateFlow<TextListUiState>
) {

    val currentState: TextListUiState
        get() = state.value

    fun update(
        transform: (TextListUiState) -> TextListUiState
    ) {
        state.update(transform)
    }
}