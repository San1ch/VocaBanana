package com.san1ch.vocabanana.feature.text.presentation.textlist.handler

import com.san1ch.vocabanana.core.essentials.Logger
import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import com.san1ch.vocabanana.core.essentials.model.word.WordQuery
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import com.san1ch.vocabanana.core.essentials.usecases.GetWordsUseCase
import com.san1ch.vocabanana.core.ui.state.Resource
import com.san1ch.vocabanana.core.ui.state.ResourceError
import com.san1ch.vocabanana.core.ui.state.getOrNull
import com.san1ch.vocabanana.feature.text.domain.ReadingStateRepository
import com.san1ch.vocabanana.feature.text.domain.usecase.GetTextListItemUseCase
import com.san1ch.vocabanana.feature.text.presentation.data.TextToken
import com.san1ch.vocabanana.feature.text.presentation.data.tokenize
import com.san1ch.vocabanana.feature.text.presentation.model.TextWithContent
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiEffect
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class TextListReaderHandler @Inject constructor(
    private val textRepository: TextRepository,
    private val getTextListItemUseCase: GetTextListItemUseCase,
    private val readingStateRepository: ReadingStateRepository,
    private val wordRepository: WordRepository,
    private val getWordsUseCase: GetWordsUseCase,
    private val logger: Logger,
) {

    private var saveJob: Job? = null

    fun handle(
        intent: TextListUiIntent.Reader,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
        scope: CoroutineScope,
        currentState: TextListUiState,
        sendEffect: (TextListUiEffect) -> Unit,
    ) {
        when (intent) {
            is TextListUiIntent.Reader.SelectText -> {
                selectText(
                    textId = intent.id,
                    updateState = updateState,
                    scope = scope,
                    sendEffect = sendEffect,
                )
            }

            is TextListUiIntent.Reader.UpdateProgress -> {
                updateProgress(
                    textId = intent.id,
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

            is TextListUiIntent.Reader.ChangeWordState -> {
                saveNewWordState(
                    id = intent.id,
                    state = intent.state,
                    scope = scope,
                )
            }
        }
    }

    private fun saveNewWordState(id: Int, state: WordState, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val previousWord = getWordsUseCase(WordQuery(wordIds = FilterType.Include(listOf(id))))
                .firstOrNull()?.first() ?: run {
                logger.e(IllegalStateException("Invariant broken"), "Failed to find word for update with id=$id")
                return@launch
            }

            wordRepository.updateWord(previousWord.withState(state))
        }
    }

    private fun selectText(
        textId: Int,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
        scope: CoroutineScope,
        sendEffect: (TextListUiEffect) -> Unit,
    ) {
        scope.launch(Dispatchers.IO) {
            if (!textRepository.isTextIdExists(textId)) {
                logger.d("Text with id=$textId does not exist, redirecting to generation.")
                updateState { it.copy(currentIdToGenerateWords = textId) }
                return@launch
            }

            updateState { it.copy(selectedText = Resource.Loading) }

            sendEffect(TextListUiEffect.NavigateToReader)
            try {
                val text = getTextListItemUseCase(textId).first()
                val content = textRepository.getContentById(textId).first()

                println("SELECT TEXT: Id-$textId: ${text.lastScrollPosition}")
                val allWords = content.flatMap { it.tokenize() }
                    .filterIsInstance<TextToken.Word>()
                    .map { it.text.lowercase() }
                    .distinct()

                println("SELECT TEXT: Id-$textId: ${text.lastScrollPosition}")
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
                println("SELECT TEXT: Id-$textId: ${text.lastScrollPosition}")
                updateState {
                    it.copy(
                        selectedText = Resource.Success(TextWithContent(text, enrichedContent)),
                    )
                }
                println("SELECT TEXT: Id-$textId: ${text.lastScrollPosition}")
                println("SELECT TEXT: FINISHED")
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
        textId: Int,
        progress: Float,
        scope: CoroutineScope,
    ) {
        saveJob?.cancel()

        saveJob = scope.launch(Dispatchers.IO) {
            delay(500)

            readingStateRepository.updateProgress(textId, System.currentTimeMillis(), progress)
        }
    }

    private fun saveFilterStates(
        states: Set<WordState>,
        scope: CoroutineScope,
        currentState: TextListUiState,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        val selectedTextId: Int = currentState.selectedText.getOrNull { it.id } ?: run {
            logger.d("Attempted to save filter/settings, but no text is currently selected.")
            return
        }
        val currentSuccess = currentState.selectedText as? Resource.Success ?: run {
            logger.d("Attempted to save filter/settings, but selectedText is not a success resource.")
            return
        }

        updateState { state ->
            state.copy(
                selectedText = Resource.Success(data = currentSuccess.data.copy(text = currentSuccess.data.text.copy(activeWordStates = states))),
            )
        }

        scope.launch(Dispatchers.IO) {
            readingStateRepository.updateWordStates(selectedTextId, states)
        }
    }

    private fun saveReaderSettings(
        settings: TextAppearanceSettings,
        scope: CoroutineScope,
        currentState: TextListUiState,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        val selectedTextId: Int = currentState.selectedText.getOrNull { it.id } ?: run {
            logger.d("Attempted to save filter/settings, but no text is currently selected.")
            return
        }
        val currentSuccess = currentState.selectedText as? Resource.Success ?: run {
            logger.d("Attempted to save filter/settings, but selectedText is not a success resource.")
            return
        }

        updateState { state ->
            state.copy(
                selectedText = Resource.Success(data = currentSuccess.data.copy(text = currentSuccess.data.text.copy(textAppearanceSettings = settings))),
            )
        }

        scope.launch {
            readingStateRepository.updateAppearance(selectedTextId, settings)
        }
    }
}
