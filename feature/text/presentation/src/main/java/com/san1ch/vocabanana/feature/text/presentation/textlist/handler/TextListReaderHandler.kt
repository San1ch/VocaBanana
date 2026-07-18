package com.san1ch.vocabanana.feature.text.presentation.textlist.handler

import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import com.san1ch.vocabanana.feature.text.domain.ReadingStateRepository
import com.san1ch.vocabanana.feature.text.domain.usecase.GetTextListItemUseCase
import com.san1ch.vocabanana.feature.text.presentation.data.TextToken
import com.san1ch.vocabanana.feature.text.presentation.data.tokenize
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class TextListReaderHandler @Inject constructor(
    private val textRepository: TextRepository,
    private val getTextListItemUseCase: GetTextListItemUseCase,
    private val readingStateRepository: ReadingStateRepository,
    private val wordRepository: WordRepository,
) {

    private var saveJob: Job? = null

    fun handle(
        intent: TextListUiIntent.Reader,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
        scope: CoroutineScope,
    ) {
        when (intent) {
            is TextListUiIntent.Reader.SelectText -> {
                selectText(
                    id = intent.id,
                    updateState = updateState,
                    scope = scope,
                )
            }

            TextListUiIntent.Reader.ClearSelection -> {
                clearText(
                    updateState = updateState,
                )
            }

            is TextListUiIntent.Reader.UpdateProgress -> {
                updateProgress(
                    id = intent.id,
                    progress = intent.progress,
                    scope = scope,
                )
            }

            TextListUiIntent.Reader.ToggleLock -> {
                updateState {
                    it.copy(
                        isLockedByReaderLocker = !it.isLockedByReaderLocker,
                    )
                }
            }

            TextListUiIntent.Reader.NotifySwipeBlocked -> {
                updateState {
                    it.copy(
                        isSwipeAttempted = true,
                    )
                }
            }

            TextListUiIntent.Reader.ResetSwipeAttempt -> {
                updateState {
                    it.copy(
                        isSwipeAttempted = false,
                    )
                }
            }
            is TextListUiIntent.Reader.ChangeWordStates -> {
                saveFilterStates(
                    states = intent.states,
                    updateState = updateState,
                    scope = scope,
                )
            }
            is TextListUiIntent.Reader.ChangePageSettings -> {
                saveReaderSettings(
                    settings = intent.settings,
                    updateState = updateState,
                    scope = scope,
                )
            }
        }
    }
    private fun selectText(
        id: Int,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
        scope: CoroutineScope,
    ) {
        scope.launch(Dispatchers.IO) {
            clearText(updateState)

            val textFlow = getTextListItemUseCase(id)
            val contentFlow = textRepository.getContentById(id)

            combine(textFlow, contentFlow) { text, content ->
                Pair(text, content)
            }.collect { (text, content) ->
                val allWords = content.flatMap { it.tokenize() }
                    .filterIsInstance<TextToken.Word>()
                    .map { it.text.lowercase() }
                    .distinct()

                val statesMap = wordRepository.getWordStatesMapForText(allWords)

                val enrichedContent = content.map { paragraph ->
                    paragraph.tokenize().map { token ->
                        if (token is TextToken.Word) {
                            token.copy(state = statesMap[token.text.lowercase()])
                        } else {
                            token
                        }
                    }
                }

                updateState {
                    it.copy(
                        selectedText = text,
                        textContent = enrichedContent,
                    )
                }
            }
        }
    }

    private fun clearText(
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        updateState {
            it.copy(
                selectedText = null,
                textContent = emptyList(),
            )
        }
    }
    private fun updateProgress(
        id: Int,
        progress: Float,
        scope: CoroutineScope,
    ) {
        saveJob?.cancel()

        saveJob = scope.launch(Dispatchers.IO) {
            delay(500)

            readingStateRepository.updateReadingState(id) { readingState ->
                readingState.copy(
                    lastScrollPosition = progress,
                    lastReadTime = System.currentTimeMillis(),
                )
            }
        }
    }
    private fun saveFilterStates(
        states: Set<WordState>,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
        scope: CoroutineScope,
    ) {
        updateState { it.copy(selectedText = it.selectedText?.copy(activeWordStates = states)) }

        var selectedTextId: Int? = null
        updateState { state ->
            selectedTextId = state.selectedText?.id
            state
        }

        selectedTextId ?: return

        scope.launch(Dispatchers.IO) {
            readingStateRepository.updateReadingState(selectedTextId!!) { readingState ->
                readingState.copy(
                    activeWordStates = states.toSet(),
                )
            }
        }
    }
    private fun saveReaderSettings(
        settings: TextAppearanceSettings,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
        scope: CoroutineScope,
    ) {
        var selectedTextId: Int? = null

        updateState { state ->
            selectedTextId = state.selectedText?.id
            state
        }

        selectedTextId ?: return

        scope.launch {
            readingStateRepository.updateReadingState(selectedTextId!!) { readingState ->
                readingState.copy(
                    fontSize = settings.fontSize,
                    lineSpacing = settings.lineSpacing,
                    paragraphSpacing = settings.paragraphSpacing,
                    horizontalPadding = settings.horizontalPadding,
                )
            }
        }
    }
}
