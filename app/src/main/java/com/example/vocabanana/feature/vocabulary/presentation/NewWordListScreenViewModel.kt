package com.example.vocabanana.feature.vocabulary.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.asUiState
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.feature.text.presentation.data.toUi
import com.example.vocabanana.core.word.domain.model.WordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewWordListScreenViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val logger: Logger
) : BaseViewModel() {

    val newWords = wordRepository.getWordByStates(listOf(WordState.NEW))
        .map { list -> list.map { it.toUi() } }
        .asUiState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun updateWordState(wordId: Int, newState: WordState) {
        logger.d("Update word state: $wordId -> $newState", tag = "MyOwnTag")
        viewModelScope.launch(Dispatchers.IO) {
            val word = wordRepository.getWordById(wordId)
            word?.let {
                wordRepository.updateWord(it.withState(newState))
            }
        }
    }

}

