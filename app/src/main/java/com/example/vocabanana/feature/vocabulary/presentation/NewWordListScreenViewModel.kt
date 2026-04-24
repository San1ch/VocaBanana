package com.example.vocabanana.feature.vocabulary.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.UiEvent
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.core.word.domain.model.WordState
import com.example.vocabanana.feature.text.presentation.data.WordFilter
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.feature.text.presentation.data.filterAndSort
import com.example.vocabanana.feature.text.presentation.data.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val logger: Logger
) : BaseViewModel() {

    // Internal UI State flows
    private val _wordFilter = MutableStateFlow(WordFilter())

    // The Main State: Combined and Optimized
    @OptIn(kotlinx.coroutines.FlowPreview::class)
    val uiState = combine(
        wordRepository.getWordByStates(listOf(WordState.NEW)),
        _wordFilter.debounce(300)
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

            NewWordListIntent.NavigateBack -> sendEvent(UiEvent.NavigateBack)
        }
    }

    private fun updateWordState(wordId: Int, newState: WordState) {
        logger.d("Update word state: $wordId -> $newState", tag = "MyOwnTag")
        viewModelScope.launch(Dispatchers.IO) {
            wordRepository.getWordById(wordId)?.let {
                wordRepository.updateWord(it.withState(newState))
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