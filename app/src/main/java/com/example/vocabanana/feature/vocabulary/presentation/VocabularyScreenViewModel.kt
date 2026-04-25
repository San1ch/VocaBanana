package com.example.vocabanana.feature.vocabulary.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.domain.model.fold
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.UiEvent.NavigateTo
import com.example.vocabanana.core.presentation.UiEvent.ShowToast
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.core.word.domain.model.WordState
import com.example.vocabanana.core.word.mapper.toUiText
import com.example.vocabanana.feature.text.presentation.data.SortType
import com.example.vocabanana.feature.text.presentation.data.WordFilter
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.feature.text.presentation.data.filterAndSort
import com.example.vocabanana.feature.text.presentation.data.toDomain
import com.example.vocabanana.feature.text.presentation.data.toUi
import com.example.vocabanana.feature.vocabulary.data.VocabularyStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyScreenViewModel @Inject constructor(
    private val wordRepository: WordRepository,
) : BaseViewModel() {

    private val _wordFilter = MutableStateFlow(WordFilter())
    private val _selectedWordId = MutableStateFlow<Int?>(null)

    // The single stream of state for the entire screen
    val uiState = combine(
        wordRepository.getAllLemmas(),
        wordRepository.getCountByStates(listOf(WordState.NEW)),
        _wordFilter,
        _selectedWordId
    ) { allLemmas, newCount, filter, selectedId ->

        val validLemmas = allLemmas.filter { it.state != WordState.NEW }

        VocabularyUiState(
            wordsState = UiState.Success(
                validLemmas.map { it.toUi() }.filterAndSort(filter)
            ),
            stats = VocabularyStats(
                totalLemmas = validLemmas.size,
                known = validLemmas.count { it.state == WordState.KNOWN },
                learning = validLemmas.count { it.state == WordState.LEARNING },
                notKnown = validLemmas.count { it.state == WordState.NOT_KNOWN },
                ignored = validLemmas.count { it.state == WordState.IGNORED }
            ),
            wordFilter = filter,
            selectedWordId = selectedId,
            newWordsCount = newCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VocabularyUiState()
    )

    fun onIntent(intent: VocabularyIntent) {
        when (intent) {
            is VocabularyIntent.SelectWord -> _selectedWordId.value = intent.id
            is VocabularyIntent.UpdateWord -> updateWord(intent.word)
            is VocabularyIntent.DeleteWord -> deleteWord(intent.id)
            is VocabularyIntent.UpdateSearchQuery -> _wordFilter.update { it.copy(searchQuery = intent.searchQuery) }
            is VocabularyIntent.ChangeSortType -> _wordFilter.update { it.copy(sortType = intent.sortType) }
            VocabularyIntent.ToggleSortOrder -> _wordFilter.update { it.copy(isAscending = !it.isAscending) }
            VocabularyIntent.NavigateToNewWords -> sendEvent(NavigateTo(AppDestination.NewWordList))
            VocabularyIntent.DeselectWord -> _selectedWordId.value = null
        }
    }

    private fun deleteWord(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            wordRepository.changeState(id, WordState.IGNORED)
        }
    }

    private fun updateWord(word: WordUi) {
        word.toDomain().fold(
            onSuccess = { viewModelScope.launch(Dispatchers.IO) { wordRepository.updateWord(it) } },
            onError = { sendEvent(ShowToast(it.toUiText())) }
        )
    }
}

data class VocabularyUiState(
    val wordsState: UiState<List<WordUi>> = UiState.Loading,
    val stats: VocabularyStats = VocabularyStats(),
    val wordFilter: WordFilter = WordFilter(),
    val selectedWordId: Int? = null,
    val newWordsCount: Int = 0
)

sealed interface VocabularyIntent {
    data class SelectWord(val id: Int) : VocabularyIntent
    object DeselectWord : VocabularyIntent
    data class DeleteWord(val id: Int) : VocabularyIntent
    data class ChangeSortType(val sortType: SortType) : VocabularyIntent
    data class UpdateSearchQuery(val searchQuery: String) : VocabularyIntent
    data object ToggleSortOrder : VocabularyIntent
    data object NavigateToNewWords : VocabularyIntent
    data class UpdateWord(val word: WordUi) : VocabularyIntent
}