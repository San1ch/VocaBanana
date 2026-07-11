package com.san1ch.vocabanana.feature.vocabulary.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import com.san1ch.vocabanana.core.essentials.model.word.WordQuery
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import com.san1ch.vocabanana.core.essentials.usecases.GetWordsWithCountUseCase
import com.san1ch.vocabanana.core.ui.BaseViewModel
import com.san1ch.vocabanana.core.ui.model.SortType
import com.san1ch.vocabanana.core.ui.model.WordFilter
import com.san1ch.vocabanana.core.ui.model.WordUi
import com.san1ch.vocabanana.core.ui.model.filterAndSort
import com.san1ch.vocabanana.core.ui.model.toUi
import com.san1ch.vocabanana.core.ui.state.UiState
import com.san1ch.vocabanana.feature.vocabulary.presentation.router.VocabularyRouter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewWordListScreenViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val vocabularyRouter: VocabularyRouter,
    private val getWordWithCountUseCase: GetWordsWithCountUseCase
) : BaseViewModel() {

    // Internal UI State flows
    private val _wordFilter = MutableStateFlow(WordFilter(sortType = SortType.COUNT))
    @OptIn(FlowPreview::class)
    private val debounceWordFilter = _wordFilter.debounce(500).distinctUntilChanged()

    // The Main State: Combined and Optimized
    val uiState = combine(
        getWordWithCountUseCase(WordQuery(states = FilterType.Include(listOf(WordState.NEW)))),
        debounceWordFilter
    ) { rawList, filterData ->
        // Filter only NEW words, Transform to UI object and Filter
        val filteredList = rawList
            .filter { word ->
                word.state == WordState.NEW
            }
            .map { it.toUi() }
            .filterAndSort(filterData)

        NewWordListState(
            words = UiState.Success(filteredList),
            filter = filterData
        )
    }
        .distinctUntilChanged() // Only emit if the actual data content changes
        .flowOn(Dispatchers.Default) // Move heavy computation off the Main thread
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NewWordListState(words = UiState.Loading)
        )

    /**
     * Single entry point for all UI actions
     */
    fun onIntent(intent: NewWordListIntent) {
        when (intent) {
            is NewWordListIntent.UpdateSearchQuery -> {
                _wordFilter.value = _wordFilter.value.copy(searchQuery = intent.query)
            }

            is NewWordListIntent.ChangeWordState -> {
                updateWordState(intent.wordId, intent.newState)
            }

            NewWordListIntent.NavigateBack -> vocabularyRouter.navigateBack()
        }
    }

    private fun updateWordState(wordId: Int, newState: WordState) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = wordRepository.getWordById(wordId)
            result.onSuccess { word ->
                wordRepository.updateWord(word.withState(newState))
            }
        }
    }
}

sealed interface NewWordListIntent {
    data class UpdateSearchQuery(val query: String) : NewWordListIntent
    data class ChangeWordState(val wordId: Int, val newState: WordState) : NewWordListIntent
    object NavigateBack : NewWordListIntent
}

data class NewWordListState(
    val words: UiState<List<WordUi>> = UiState.Loading,
    val filter: WordFilter = WordFilter()
)