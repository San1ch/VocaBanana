package com.san1ch.vocabanana.feature.text.presentation.textlist.handler

import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.feature.text.domain.ReadingStateRepository
import com.san1ch.vocabanana.feature.text.domain.usecase.GetTextListItemUseCase
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class TextListReaderHandler @Inject constructor(
    private val textRepository: TextRepository,
    private val getTextListItemUseCase: GetTextListItemUseCase,
    private val readingStateRepository: ReadingStateRepository,
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
                updateState {
                    it.copy(
                        selectedText = null,
                        textContent = emptyList(),
                    )
                }
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
                        isLocked = !it.isLocked,
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
            launch {
                getTextListItemUseCase(id).collect { text ->
                    updateState {
                        it.copy(
                            selectedText = text,
                        )
                    }
                }
            }

            launch {
                textRepository.getContentById(id).collect { content ->
                    updateState {
                        it.copy(
                            textContent = content,
                        )
                    }
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
