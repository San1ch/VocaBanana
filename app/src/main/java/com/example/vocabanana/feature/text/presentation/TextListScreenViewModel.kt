package com.example.vocabanana.feature.text.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.UiEvent.NavigateTo
import com.example.vocabanana.core.presentation.UiEvent.OpenUrl
import com.example.vocabanana.feature.text.domain.GenerateWordsFromTextUseCase
import com.example.vocabanana.feature.text.presentation.data.GenerateWordsFromTextUiState
import com.example.vocabanana.feature.text.presentation.data.TextPreview
import com.example.vocabanana.feature.text.presentation.data.TextUi
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.feature.text.presentation.data.toPreview
import com.example.vocabanana.feature.text.presentation.data.toUi
import com.example.vocabanana.feature.text.presentation.data.toUiState
import com.example.vocabanana.feature.text.presentation.textlistscreen.ReaderSettings
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
            // --- Navigation & Global ---
            is TextListUiIntent.NavigateToAddText -> sendEvent(NavigateTo(AppDestination.AddText))
            is TextListUiIntent.PageChanged -> updatePage(intent.page)

            // --- Text Selection & Reading ---
            is TextListUiIntent.SelectText -> selectText(intent.id)
            is TextListUiIntent.ClearSelection -> clearSelection()
            is TextListUiIntent.UpdateProgress -> updateProgress(intent.id, intent.progress)
            is TextListUiIntent.ToggleLock -> toggleLock()

            // --- Word & Dictionary Logic ---
            is TextListUiIntent.WordClicked -> selectWordInPage(intent.word)
            is TextListUiIntent.CloseWordInfo -> closeWordInfo()
            is TextListUiIntent.OxfordMoreInfo -> openOxfordDictionary(intent.word)
            is TextListUiIntent.GenerateWords -> generateWords()

            // --- Reader Settings ---
            is TextListUiIntent.ShowRenderSettings -> setRenderSettingsVisibility(true)
            is TextListUiIntent.CloseReaderSettings -> setRenderSettingsVisibility(false)
            is TextListUiIntent.ChangePageSettings -> {
                _uiState.update { it.copy(readerSettings = intent.settings) }
            }

            // --- Deletion Logic ---
            is TextListUiIntent.SelectTextToDelete -> selectTextToDelete(intent.textId)
            is TextListUiIntent.ClearTextIdToDelete -> clearTextIdToDelete()
            is TextListUiIntent.DeleteText -> deleteText()

            // --- Swipe/UI Feedback ---
            is TextListUiIntent.NotifySwipeBlocked -> notifySwipeBlocked()
            is TextListUiIntent.ResetSwipeAttempt -> resetSwipeAttempt()
        }
    }

    // --- Private Logic (Reducers) ---

    // --- Reading Mode & Content Selection ---
    private fun observeTexts() {
        viewModelScope.launch {
            textRepository.getTexts().collect { list ->
                _uiState.update { it.copy(textItems = list.map { it.toPreview() }) }
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

    private fun updateProgress(textId: Int, position: Float) {
        saveJob?.cancel()
        saveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            textRepository.updateProgress(textId, position, System.currentTimeMillis())
        }
    }

    // --- Word Info & Dictionary ---
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

    private fun openOxfordDictionary(word: String) {
        val baseUrl = "https://www.oxfordlearnersdictionaries.com/definition/english/"
        val formattedWord = word.trim().lowercase().replace(" ", "-")
        sendEvent(OpenUrl("$baseUrl$formattedWord"))
    }

    private fun generateWords() {
        val textId = _uiState.value.selectedText?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            generateWordsFromText(textId).collect { result ->
                _uiState.update { it.copy(generatingState = result.toUiState()) }
            }
        }
    }

    // --- UI Controls & Settings ---
    private fun setRenderSettingsVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(showSettings = isVisible) }
    }

    private fun toggleLock() {
        _uiState.update { it.copy(isLocked = !it.isLocked) }
    }

    private fun updatePage(page: Int) {
        _uiState.update { it.copy(pagerPage = page) }
    }

    // --- Deletion Flow ---
    private fun selectTextToDelete(textId: Int) {
        _uiState.update { it.copy(selectedTextIdToDelete = textId) }
    }

    private fun clearTextIdToDelete() {
        _uiState.update { it.copy(selectedTextIdToDelete = null) }
    }

    private fun deleteText() {
        viewModelScope.launch(Dispatchers.IO) {
            val idToDelete = _uiState.value.selectedTextIdToDelete ?: return@launch
            textRepository.deleteText(idToDelete)
            clearTextIdToDelete()
        }
    }

    // --- Swipe Feedback ---
    private fun notifySwipeBlocked() {
        _uiState.update { it.copy(isSwipeAttempted = true) }
    }

    private fun resetSwipeAttempt() {
        _uiState.update { it.copy(isSwipeAttempted = false) }
    }
}

sealed class TextListUiIntent {

    // --- Navigation & Global ---
    object NavigateToAddText : TextListUiIntent()
    data class PageChanged(val page: Int) : TextListUiIntent()

    // --- List & Selection ---
    data class SelectText(val id: Int) : TextListUiIntent()
    object ClearSelection : TextListUiIntent()

    // --- Deletion Flow ---
    data class SelectTextToDelete(val textId: Int) : TextListUiIntent()
    object ClearTextIdToDelete : TextListUiIntent()
    object DeleteText : TextListUiIntent()

    // --- Reader Page Interactions ---
    data class UpdateProgress(val id: Int, val progress: Float) : TextListUiIntent()
    object ToggleLock : TextListUiIntent()
    object NotifySwipeBlocked : TextListUiIntent()
    object ResetSwipeAttempt : TextListUiIntent()

    // --- Word & Dictionary Logic ---
    data class WordClicked(val word: String) : TextListUiIntent()
    object CloseWordInfo : TextListUiIntent()
    data class OxfordMoreInfo(val word: String) : TextListUiIntent()
    object GenerateWords : TextListUiIntent()

    // --- Reader Settings ---
    object ShowRenderSettings : TextListUiIntent()
    object CloseReaderSettings : TextListUiIntent()
    data class ChangePageSettings(val settings: ReaderSettings) : TextListUiIntent()
}

data class TextListUiState(
    // --- Navigation & Global Layout ---
    val pagerPage: Int = 0,
    val showSettings: Boolean = false,
    val isLocked: Boolean = false,

    // --- Content (The "List" and the "Reader") ---
    val textItems: List<TextPreview> = emptyList(),
    val selectedText: TextUi? = null,
    val readerSettings: ReaderSettings = ReaderSettings(),

    // --- Word & Dictionary Logic ---
    val wordInfoState: WordInfoState = WordInfoState.Hidden,
    val generatingState: GenerateWordsFromTextUiState? = null,

    // --- Operations & Feedback (Temporary States) ---
    val selectedTextIdToDelete: Int? = null,
    val isSwipeAttempted: Boolean = false
)

sealed class WordInfoState {
    object Hidden : WordInfoState()
    object Loading : WordInfoState()
    data class Found(val word: WordUi) : WordInfoState()
    data class NotFound(val word: String) : WordInfoState()
}