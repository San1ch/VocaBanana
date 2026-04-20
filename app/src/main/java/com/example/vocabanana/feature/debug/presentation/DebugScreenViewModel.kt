package com.example.vocabanana.feature.debug.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.asUiState
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.feature.text.presentation.data.toPreview
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
    private val textRepository: TextRepository,
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