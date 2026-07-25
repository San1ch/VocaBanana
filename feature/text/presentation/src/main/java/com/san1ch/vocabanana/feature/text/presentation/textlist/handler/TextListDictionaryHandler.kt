package com.san1ch.vocabanana.feature.text.presentation.textlist.handler

import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import com.san1ch.vocabanana.core.essentials.model.word.WordQuery
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import com.san1ch.vocabanana.core.essentials.usecases.GetWordsWithCountUseCase
import com.san1ch.vocabanana.core.ui.model.UiEvent
import com.san1ch.vocabanana.core.ui.model.toUi
import com.san1ch.vocabanana.core.ui.state.getOrNull
import com.san1ch.vocabanana.feature.text.domain.usecase.GenerateWordsFromTextUseCase
import com.san1ch.vocabanana.feature.text.presentation.mapper.GenerateWordsFromTextUiMapper
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiState
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.WordInfoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class TextListDictionaryHandler @Inject constructor(
    private val wordRepository: WordRepository,
    private val textRepository: TextRepository,
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
                selectPopExtraInfo(
                    word = intent.word,
                    scope = scope,
                    updateState = updateState,
                    textId = state.selectedText.getOrNull { it.id } ?: return, // TODO: handle null
                )
            }

            TextListUiIntent.Dictionary.CloseWordInfo -> {
                updateState {
                    it.copy(
                        showWordInfoState = WordInfoState.Hidden,
                    )
                }
            }

            is TextListUiIntent.Dictionary.OxfordMoreInfo -> {
                openOxfordDictionary(intent.word, sendEvent)
            }

            is TextListUiIntent.Dictionary.GenerateWords -> {
                generateWords(
                    textId = intent.textId,
                    scope = scope,
                    updateState = updateState,
                )
            }

            TextListUiIntent.Dictionary.ClearIdToGenereteWords -> {
                updateState {
                    it.copy(
                        currentIdToGenerateWords = null,
                    )
                }
            }
        }
    }
    private fun selectPopExtraInfo(
        word: String,
        textId: Int,
        scope: CoroutineScope,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        updateState {
            it.copy(
                showWordInfoState = WordInfoState.Loading,
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

                    val textWordCount = textRepository.getWordCountInText(wordId, textId) ?: return@launch

                    updateState {
                        it.copy(
                            showWordInfoState = WordInfoState.Found(wordUi, textWordCount),
                        )
                    }
                }
                .onFailure {
                    updateState {
                        it.copy(
                            showWordInfoState = WordInfoState.NotFound(word),
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
