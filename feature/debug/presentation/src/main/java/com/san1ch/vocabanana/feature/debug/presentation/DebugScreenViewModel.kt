package com.san1ch.vocabanana.feature.debug.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import com.san1ch.vocabanana.core.ui.BaseViewModel
import com.san1ch.vocabanana.core.ui.state.UiState
import com.san1ch.vocabanana.core.ui.state.asUiState
import com.san1ch.vocabanana.core.ui.model.toPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugScreenViewModel @Inject constructor(
    textRepository: TextRepository,
    private val wordRepository: WordRepository
) : BaseViewModel() {

    val textsState = textRepository.getTexts()
        .map { list -> list.map { it.toPreview() } }
        .asUiState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), UiState.Loading)

    private val _selectedTextId = MutableStateFlow<Int?>(null)
    val selectedTextId = _selectedTextId.asStateFlow()

    fun handleAction(action: DebugIntent) {
        when (action) {
            is DebugIntent.SelectText -> {
                _selectedTextId.value = action.id
            }

            DebugIntent.DeleteAllWords -> {
                viewModelScope.launch(Dispatchers.IO) {
                    wordRepository.deleteAllWords()
                }
            }
        }

    }


}