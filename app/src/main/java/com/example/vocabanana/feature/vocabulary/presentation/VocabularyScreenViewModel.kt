package com.example.vocabanana.feature.vocabulary.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.domain.model.fold
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.UiEvent
import com.example.vocabanana.core.presentation.asUiState
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.core.word.domain.model.WordState
import com.example.vocabanana.core.word.mapper.toUiText
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.feature.text.presentation.data.toDomain
import com.example.vocabanana.feature.text.presentation.data.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyScreenViewModel @Inject constructor(
    private val wordRepository: WordRepository,
) : BaseViewModel() {

    // Word selection
    private val _selectedWordId = MutableStateFlow<Int?>(null)
    val selectedWordId = _selectedWordId.asStateFlow()

    // UI Settings flows
    private val _sortType = MutableStateFlow(SortType.ALPHABETIC)
    val sortType = _sortType.asStateFlow()

    // Sorting order
    private val _isAscending = MutableStateFlow(true)
    val isAscending = _isAscending.asStateFlow()

    // Combining Database words with UI Sorting logic
    val words = combine(
        wordRepository.getAllLemmas(),
        _sortType,
        _isAscending
    ) { rawList, sort, ascending ->
        rawList
            .filter { it.state != WordState.NEW }
            .map { it.toUi() }
            .let { list ->
                val sortedList = when (sort) {
                    SortType.ALPHABETIC -> list.sortedBy { it.lemma }
                    SortType.STATE -> list.sortedBy { it.state.ordinal }
                    SortType.COUNT -> list.sortedBy { it.countInTheTexts }
                    SortType.DATE -> list.sortedBy { it.whenAdded }
                }

                if (ascending) sortedList else sortedList.reversed()
            }
    }
        .asUiState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun onIntent(intent: VocabularyIntent) {
        when (intent) {
            is VocabularyIntent.SelectWord -> _selectedWordId.update { intent.id }
            is VocabularyIntent.DeleteWord -> viewModelScope.launch(Dispatchers.IO) {
                wordRepository.deleteById(intent.id)
            }

            is VocabularyIntent.ChangeSortType -> _sortType.update { intent.sortType }
            VocabularyIntent.ToggleSortOrder -> _isAscending.update { !it }
            VocabularyIntent.NavigateToNewWords -> sendEvent(UiEvent.NavigateTo(AppDestination.NewWordList))
        }
    }


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