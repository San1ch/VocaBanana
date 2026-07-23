package com.san1ch.vocabanana.feature.text.presentation.textlist.handler

import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import com.san1ch.vocabanana.core.ui.state.Resource
import com.san1ch.vocabanana.core.ui.state.ResourceError
import com.san1ch.vocabanana.core.ui.state.getOrNull
import com.san1ch.vocabanana.feature.text.domain.ReadingStateRepository
import com.san1ch.vocabanana.feature.text.domain.usecase.GetTextListItemUseCase
import com.san1ch.vocabanana.feature.text.presentation.data.TextToken
import com.san1ch.vocabanana.feature.text.presentation.data.tokenize
import com.san1ch.vocabanana.feature.text.presentation.model.TextWithContent
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
        currentState: TextListUiState,
    ) {
        when (intent) {
            is TextListUiIntent.Reader.SelectText -> {
                selectText(
                    id = intent.id,
                    updateState = updateState,
                    scope = scope,
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
                    scope = scope,
                    currentState = currentState,
                    updateState = updateState,
                )
            }

            is TextListUiIntent.Reader.ChangePageSettings -> {
                saveReaderSettings(
                    settings = intent.settings,
                    scope = scope,
                    currentState = currentState,
                    updateState = updateState,
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
            updateState { it.copy(selectedText = Resource.Loading) }

            try {
                val text = getTextListItemUseCase(id).first()
                val content = textRepository.getContentById(id).first()

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
                        selectedText = Resource.Success(TextWithContent(text, enrichedContent)),
                    )
                }
            } catch (e: Exception) {
                updateState {
                    it.copy(
                        selectedText = Resource.Error(ResourceError.Unknown(e.message ?: "Unknown error")),
                    )
                }
            }
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
        scope: CoroutineScope,
        currentState: TextListUiState,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        val selectedTextId: Int = currentState.selectedText.getOrNull { it.id } ?: return

        val currentSuccess = currentState.selectedText as? Resource.Success ?: return
        updateState{ state ->
            state.copy(
                selectedText = Resource.Success(data = currentSuccess.data.copy(text = currentSuccess.data.text.copy(activeWordStates = states))),
            )
        }

        scope.launch(Dispatchers.IO) {


            readingStateRepository.updateReadingState(selectedTextId) { readingState ->
                readingState.copy(
                    activeWordStates = states.toSet(),
                )
            }
        }
    }

    private fun saveReaderSettings(
        settings: TextAppearanceSettings,
        scope: CoroutineScope,
        currentState: TextListUiState,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        val selectedTextId: Int = currentState.selectedText.getOrNull { it.id } ?: return

        val currentSuccess = currentState.selectedText as? Resource.Success ?: return
        updateState{ state ->
            state.copy(
                selectedText = Resource.Success(data = currentSuccess.data.copy(text = currentSuccess.data.text.copy(textAppearanceSettings = settings))),
            )
        }

        scope.launch {
            readingStateRepository.updateReadingState(selectedTextId) { readingState ->
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
