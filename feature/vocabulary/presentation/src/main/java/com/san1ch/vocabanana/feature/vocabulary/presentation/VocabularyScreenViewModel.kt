package com.san1ch.vocabanana.feature.vocabulary.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.model.fold
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import com.san1ch.vocabanana.core.essentials.usecases.GetWordsWithCountUseCase
import com.san1ch.vocabanana.core.ui.BaseViewModel
import com.san1ch.vocabanana.core.ui.model.SortType
import com.san1ch.vocabanana.core.ui.model.UiEvent
import com.san1ch.vocabanana.core.ui.state.UiState
import com.san1ch.vocabanana.core.ui.model.WordFilter
import com.san1ch.vocabanana.core.ui.model.WordUi
import com.san1ch.vocabanana.core.ui.model.filterAndSort
import com.san1ch.vocabanana.core.ui.model.toDomain
import com.san1ch.vocabanana.core.ui.model.toUi
import com.san1ch.vocabanana.feature.vocabulary.presentation.router.VocabularyRouter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyScreenViewModel @Inject constructor(
    private val getWordsWithCountUseCase: GetWordsWithCountUseCase,
    private val wordRepository: WordRepository,
    private val vocabularyRouter: VocabularyRouter
) : BaseViewModel() {

    private val _wordFilter = MutableStateFlow(WordFilter())
    @OptIn(FlowPreview::class)
    private val debounceWordFilter = _wordFilter.debounce(500).distinctUntilChanged()
    private val _selectedWordId = MutableStateFlow<Int?>(null)

    // The single stream of state for the entire screen
    val uiState = combine(
        getWordsWithCountUseCase(),
        debounceWordFilter,
        _selectedWordId
    ) { allLemmas, filter, selectedId ->

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
            newWordsCount = allLemmas.count { it.state == WordState.NEW }
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
            is VocabularyIntent.NavigateBack -> vocabularyRouter.navigateBack()
            is VocabularyIntent.DeleteWord -> deleteWord(intent.id)
            is VocabularyIntent.UpdateSearchQuery -> _wordFilter.update { it.copy(searchQuery = intent.searchQuery) }
            is VocabularyIntent.ChangeSortType -> _wordFilter.update { it.copy(sortType = intent.sortType) }
            VocabularyIntent.ToggleSortOrder -> _wordFilter.update { it.copy(isAscending = !it.isAscending) }
            VocabularyIntent.NavigateToNewWords -> vocabularyRouter.navigateToNewWords()
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
            onError = { sendEvent(UiEvent.ShowToast(it.message)) }
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
    object NavigateBack : VocabularyIntent
    data class DeleteWord(val id: Int) : VocabularyIntent
    data class ChangeSortType(val sortType: SortType) : VocabularyIntent
    data class UpdateSearchQuery(val searchQuery: String) : VocabularyIntent
    data object ToggleSortOrder : VocabularyIntent
    data object NavigateToNewWords : VocabularyIntent
    data class UpdateWord(val word: WordUi) : VocabularyIntent
}