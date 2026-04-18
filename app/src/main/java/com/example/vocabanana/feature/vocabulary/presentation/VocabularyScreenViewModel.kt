package com.example.vocabanana.feature.vocabulary.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.R
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.domain.model.ValidateResult
import com.example.vocabanana.core.domain.model.fold
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.UiEvent
import com.example.vocabanana.core.presentation.UiResult
import com.example.vocabanana.core.presentation.UiText
import com.example.vocabanana.core.presentation.asUiState
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.core.word.domain.model.WordDomain
import com.example.vocabanana.feature.text.presentation.data.toUi
import com.example.vocabanana.core.word.domain.model.WordState
import com.example.vocabanana.core.word.domain.model.WordValidateError
import com.example.vocabanana.core.word.mapper.toUiText
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.feature.text.presentation.data.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyScreenViewModel @Inject constructor(
    private val wordRepository: WordRepository,
) : BaseViewModel() {

    private val _selectedWordId = MutableStateFlow<Int?>(null)
    val selectedWordId = _selectedWordId.asStateFlow()

    // 1. Filtered list: Only show words that are NOT "NEW"
    val words = wordRepository.getAllLemmas()
        .map { list ->
            list.filter { it.state != WordState.NEW }
                .map { it.toUi() }
        }
        .asUiState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val newWordsCount = wordRepository.getCountByStates(listOf(WordState.NEW))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun selectWord(id: Int) = _selectedWordId.update { id }

    fun deleteWord(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        wordRepository.deleteById(id)
    }
    fun updateWord(word: WordUi): Boolean {
        return word.toDomain().fold(
            onSuccess = {
                viewModelScope.launch(Dispatchers.IO) {
                    wordRepository.updateWord(it)
                }
                return@fold true
            },
            onError = {
                sendEvent(UiEvent.ShowToast(it.toUiText()))
                return@fold false
            }
        )
    }
}