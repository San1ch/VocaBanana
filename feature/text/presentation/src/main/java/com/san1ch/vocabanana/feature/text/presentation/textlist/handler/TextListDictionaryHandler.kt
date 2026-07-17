package com.san1ch.vocabanana.feature.text.presentation.textlist.handler

import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import com.san1ch.vocabanana.core.essentials.model.word.WordQuery
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import com.san1ch.vocabanana.core.essentials.usecases.GetWordsWithCountUseCase
import com.san1ch.vocabanana.core.ui.model.UiEvent
import com.san1ch.vocabanana.core.ui.model.toUi
import com.san1ch.vocabanana.feature.text.domain.usecase.GenerateWordsFromTextUseCase
import com.san1ch.vocabanana.feature.text.presentation.mapper.GenerateWordsFromTextUiMapper
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiState
import com.san1ch.vocabanana.feature.text.presentation.textlist.WordInfoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class TextListDictionaryHandler @Inject constructor(
    private val wordRepository: WordRepository,
    private val getWordsWithCountUseCase: GetWordsWithCountUseCase,
    private val generateWordsFromText: GenerateWordsFromTextUseCase,
    private val generateWordsFromTextUiMapper: GenerateWordsFromTextUiMapper,
) {

    fun handle(
        intent: TextListUiIntent.Dictionary,
        scope: CoroutineScope,
        state: TextListUiState,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
        sendEvent: (UiEvent) -> Unit,
    ) {
        when (intent) {
            is TextListUiIntent.Dictionary.WordClicked -> {
                selectWord(
                    word = intent.word,
                    scope = scope,
                    updateState = updateState,
                )
            }

            TextListUiIntent.Dictionary.CloseWordInfo -> {
                updateState {
                    it.copy(
                        wordInfoState = WordInfoState.Hidden,
                    )
                }
            }

            is TextListUiIntent.Dictionary.OxfordMoreInfo -> {
                openOxfordDictionary(intent.word, sendEvent)
            }

            TextListUiIntent.Dictionary.GenerateWords -> {
                generateWords(
                    textId = state.selectedText?.id ?: return,
                    scope = scope,
                    updateState = updateState,
                )
            }
        }
    }
    private fun selectWord(
        word: String,
        scope: CoroutineScope,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        updateState {
            it.copy(
                wordInfoState = WordInfoState.Loading,
            )
        }

        scope.launch {
            wordRepository.getIdByWord(word)
                .onSuccess { wordId ->
                    val wordUi = getWordsWithCountUseCase(
                        WordQuery(
                            wordIds = FilterType.Include(
                                listOf(wordId),
                            ),
                        ),
                    )
                        .map { words -> words[0] }
                        .first()
                        .toUi()

                    updateState {
                        it.copy(
                            wordInfoState = WordInfoState.Found(wordUi),
                        )
                    }
                }
                .onFailure {
                    updateState {
                        it.copy(
                            wordInfoState = WordInfoState.NotFound(word),
                        )
                    }
                }
        }
    }

    private fun openOxfordDictionary(
        word: String,
        sendEvent: (UiEvent) -> Unit,
    ) {
        val baseUrl =
            "https://www.oxfordlearnersdictionaries.com/definition/english/"

        val formattedWord = word
            .trim()
            .lowercase()
            .replace(" ", "-")

        sendEvent(
            UiEvent.OpenUrl("$baseUrl$formattedWord"),
        )
    }

    private fun generateWords(
        textId: Int,
        scope: CoroutineScope,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        scope.launch {
            generateWordsFromText(textId)
                .collect { result ->
                    updateState {
                        it.copy(
                            generatingState = generateWordsFromTextUiMapper.map(result),
                        )
                    }
                }
        }
    }
}
