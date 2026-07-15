package com.san1ch.vocabanana.feature.text.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import com.san1ch.vocabanana.core.essentials.model.word.WordQuery
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import com.san1ch.vocabanana.core.essentials.usecases.GetWordsWithCountUseCase
import com.san1ch.vocabanana.core.ui.BaseViewModel
import com.san1ch.vocabanana.core.ui.model.UiEvent
import com.san1ch.vocabanana.core.ui.model.WordUi
import com.san1ch.vocabanana.core.ui.model.toUi
import com.san1ch.vocabanana.feature.text.domain.ReadingStateRepository
import com.san1ch.vocabanana.feature.text.domain.model.TextListItem
import com.san1ch.vocabanana.feature.text.domain.model.TextListPreview
import com.san1ch.vocabanana.feature.text.domain.usecase.GenerateWordsFromTextUseCase
import com.san1ch.vocabanana.feature.text.domain.usecase.GetTextListItemUseCase
import com.san1ch.vocabanana.feature.text.domain.usecase.GetTextPreviewsUseCase
import com.san1ch.vocabanana.feature.text.presentation.mapper.GenerateWordsFromTextUiMapper
import com.san1ch.vocabanana.feature.text.presentation.model.GenerateWordsFromTextUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextListScreenViewModel @Inject constructor(
    private val textRepository: TextRepository,
    private val wordRepository: WordRepository,
    private val readingStateRepository: ReadingStateRepository,
    private val generateWordsFromText: GenerateWordsFromTextUseCase,
    private val getTextPreviewsUseCase: GetTextPreviewsUseCase,
    private val getTextListItemUseCase: GetTextListItemUseCase,
    private val router: TextListRouter,
    private val generateWordsFromTextUiMapper: GenerateWordsFromTextUiMapper,
    private val getWordsWithCountUseCase: GetWordsWithCountUseCase,
) : BaseViewModel() {

    // 1. Single source of truth for the UI

    private val _uiState = MutableStateFlow(TextListUiState())
    val uiState = _uiState.asStateFlow()

    private var saveJob: Job? = null

    init {
        observeTextPreviews()
    }


    // 2. The single entry point for all UI actions
    fun onIntent(intent: TextListUiIntent) {
        when (intent) {
            // --- Navigation & Global ---
            is TextListUiIntent.NavigateToAddText -> router.navigateToAddText()
            is TextListUiIntent.PageChanged -> updatePage(intent.page)

            // --- Text Selection & Reading ---
            is TextListUiIntent.SelectText -> selectTextItemData(intent.id)
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
                saveReaderSettings(intent.settings)
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
    private fun observeTextPreviews() {
        viewModelScope.launch {
            getTextPreviewsUseCase().collect { list ->
                _uiState.update { uiState -> uiState.copy(textItems = list) }
            }
        }
    }

    private fun selectTextItemData(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                getTextListItemUseCase(id).collect { text ->
                    _uiState.update { it.copy(selectedText = text) }
                }
            }
            launch {
                textRepository.getContentById(id).collect { content ->
                    _uiState.update { it.copy(textContent = content) }
                }
            }
        }
    }

    private fun clearSelection() {
        _uiState.update { it.copy(selectedText = null, textContent = emptyList()) }
    }

    private fun updateProgress(textId: Int, position: Float) {
        saveJob?.cancel()
        saveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            readingStateRepository.updateReadingState(textId) { readingState ->
                readingState.copy(
                    lastScrollPosition = position,
                    lastReadTime = System.currentTimeMillis()
                )
            }
        }
    }

    // --- Word Info & Dictionary ---
    private fun selectWordInPage(word: String) {
        _uiState.update { it.copy(wordInfoState = WordInfoState.Loading) }

        viewModelScope.launch {
            wordRepository.getIdByWord(word)
                .onSuccess { result ->
                    val word = getWordsWithCountUseCase(
                        WordQuery(
                            wordIds = FilterType.Include<Int>(
                                listOf(result)
                            )
                        )
                    ).map {
                        it[0]
                    }.first()
                    _uiState.update {
                        it.copy(
                            wordInfoState = WordInfoState.Found(word.toUi()),
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            wordInfoState = WordInfoState.NotFound(word),
                        )
                        // TODO Process error
                    }
                }
        }
    }

    private fun closeWordInfo() {
        _uiState.update { it.copy(wordInfoState = WordInfoState.Hidden) }
    }

    private fun openOxfordDictionary(word: String) {
        val baseUrl = "https://www.oxfordlearnersdictionaries.com/definition/english/"
        val formattedWord = word.trim().lowercase().replace(" ", "-")
        sendEvent(UiEvent.OpenUrl("$baseUrl$formattedWord"))
    }

    private fun generateWords() {
        val textId = _uiState.value.selectedText?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            generateWordsFromText(textId).collect { result ->
                _uiState.update { it.copy(generatingState = generateWordsFromTextUiMapper.map(result)) }
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
            textRepository.deleteTexts(listOf(idToDelete))
            clearTextIdToDelete()
        }
    }

    private fun saveReaderSettings(settings: TextAppearanceSettings) {
        val textId = uiState.value.selectedText?.id ?: return

        viewModelScope.launch {
            readingStateRepository.updateReadingState(textId) { readingState ->
                readingState.copy(
                    fontSize = settings.fontSize,
                    lineSpacing = settings.lineSpacing,
                    paragraphSpacing = settings.paragraphSpacing,
                    horizontalPadding = settings.horizontalPadding
                )
            }
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
    data class ChangePageSettings(val settings: TextAppearanceSettings) : TextListUiIntent()
}

data class TextListUiState(
    // --- Navigation & Global Layout ---
    val pagerPage: Int = 0,
    val showSettings: Boolean = false,
    val isLocked: Boolean = false,

    // --- Content (The "List" and the "Reader") ---
    val textItems: List<TextListPreview> = emptyList(),
    val selectedText: TextListItem? = null,
    val textContent: List<String> = emptyList(),

    // --- Word & Dictionary Logic ---
    val wordInfoState: WordInfoState = WordInfoState.Hidden,
    val generatingState: GenerateWordsFromTextUiState? = null,

    // --- Operations & Feedback (Temporary States) ---
    val selectedTextIdToDelete: Int? = null,
    val isSwipeAttempted: Boolean = false,
)

sealed class WordInfoState {
    object Hidden : WordInfoState()
    object Loading : WordInfoState()
    data class Found(val word: WordUi) : WordInfoState()
    data class NotFound(val word: String) : WordInfoState()
}
