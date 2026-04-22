package com.example.vocabanana.feature.text.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.UiEvent
import com.example.vocabanana.feature.text.domain.GenerateWordsFromTextUseCase
import com.example.vocabanana.feature.text.presentation.data.GenerateWordsFromTextUiState
import com.example.vocabanana.feature.text.presentation.data.TextPreview
import com.example.vocabanana.feature.text.presentation.data.TextUi
import com.example.vocabanana.feature.text.presentation.data.toPreview
import com.example.vocabanana.feature.text.presentation.data.toUi
import com.example.vocabanana.feature.text.presentation.data.toUiState
import com.example.vocabanana.feature.text.presentation.textlistscreen.WordInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextListScreenViewModel @Inject constructor(
    private val textRepository: TextRepository,
    private val wordRepository: WordRepository,
    private val generateWordsFromText: GenerateWordsFromTextUseCase
) : BaseViewModel() {

    // 1. Single source of truth for the UI
    private val _uiState = MutableStateFlow(TextListUiState())
    val uiState = _uiState.asStateFlow()

    private var saveJob: Job? = null

    init {
        observeTexts()
    }

    // 2. The single entry point for all UI actions
    fun onIntent(intent: TextListUiIntent) {
        when (intent) {
            is TextListUiIntent.SelectText -> selectText(intent.id)
            is TextListUiIntent.ClearSelection -> clearSelection()
            is TextListUiIntent.ToggleLock -> toggleLock()
            is TextListUiIntent.NotifySwipeBlocked -> notifySwipeBlocked()
            is TextListUiIntent.ResetSwipeAttempt -> resetSwipeAttempt()
            is TextListUiIntent.PageChanged -> updatePage(intent.page)
            is TextListUiIntent.UpdateProgress -> updateProgress(intent.id, intent.progress)
            is TextListUiIntent.DeleteText -> deleteText(intent.text.id)
            is TextListUiIntent.GenerateWords -> generateWords()
            is TextListUiIntent.WordClicked -> selectWordInPage(intent.word)
            is TextListUiIntent.CloseWordInfo -> closeWordInfo()

            is TextListUiIntent.NavigateToAddText -> {
                sendEvent(UiEvent.NavigateTo(AppDestination.AddText))
            }
        }
    }

    // --- Private Logic (Reducers) ---

    private fun observeTexts() {
        viewModelScope.launch {
            textRepository.getTexts().collect { list ->
                _uiState.update { it.copy(textItems = list.map { text -> text.toPreview() }) }
            }
        }
    }

    private fun selectText(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val text = textRepository.getTextById(id).toUi()
            _uiState.update { it.copy(selectedText = text) }
        }
    }

    private fun clearSelection() {
        _uiState.update { it.copy(selectedText = null) }
    }

    private fun toggleLock() {
        _uiState.update { it.copy(isLocked = !it.isLocked) }
    }

    private fun updatePage(page: Int) {
        _uiState.update { it.copy(pagerPage = page) }
    }

    private fun notifySwipeBlocked() {
        _uiState.update { it.copy(isSwipeAttempted = true) }
    }

    private fun resetSwipeAttempt() {
        _uiState.update { it.copy(isSwipeAttempted = false) }
    }

    private fun selectWordInPage(word: String) {
        val cleanWord = word.trim().lowercase()
        _uiState.update { it.copy(wordInfoState = WordInfoState.Loading) }

        viewModelScope.launch {
            val wordDomain = wordRepository.getWordByWord(cleanWord)
            _uiState.update {
                it.copy(
                    wordInfoState = if (wordDomain != null) {
                        WordInfoState.Found(wordDomain.toUi())
                    } else {
                        WordInfoState.NotFound(cleanWord)
                    }
                )
            }
        }
    }

    private fun closeWordInfo() {
        _uiState.update { it.copy(wordInfoState = WordInfoState.Hidden) }
    }

    private fun generateWords() {
        val textId = _uiState.value.selectedText?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            generateWordsFromText(textId).collect { result ->
                _uiState.update { it.copy(generatingState = result.toUiState()) }
            }
        }
    }

    private fun updateProgress(textId: Int, position: Float) {
        saveJob?.cancel()
        saveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            textRepository.updateProgress(textId, position, System.currentTimeMillis())
        }
    }

    private fun deleteText(textId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            textRepository.deleteText(textId)
        }
    }
}

sealed class TextListUiIntent {
    data class SelectText(val id: Int) : TextListUiIntent()
    object ClearSelection : TextListUiIntent()
    object ToggleLock : TextListUiIntent()
    object NotifySwipeBlocked : TextListUiIntent()
    object ResetSwipeAttempt : TextListUiIntent()
    data class PageChanged(val page: Int) : TextListUiIntent()
    data class UpdateProgress(val id: Int, val progress: Float) : TextListUiIntent()
    data class DeleteText(val text: TextPreview) : TextListUiIntent()
    object GenerateWords : TextListUiIntent()
    data class WordClicked(val word: String) : TextListUiIntent()
    object CloseWordInfo : TextListUiIntent()
    object NavigateToAddText : TextListUiIntent()
}

data class TextListUiState(
    val pagerPage: Int = 0,
    val isLocked: Boolean = false,
    val isSwipeAttempted: Boolean = false,
    val textItems: List<TextPreview> = emptyList(),
    val selectedText: TextUi? = null,
    val generatingState: GenerateWordsFromTextUiState? = null,
    val wordInfoState: WordInfoState = WordInfoState.Hidden
)

