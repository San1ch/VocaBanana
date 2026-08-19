package com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.text.TextWordCount
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.ui.BaseViewModel
import com.san1ch.vocabanana.core.ui.model.WordUi
import com.san1ch.vocabanana.core.ui.state.Resource
import com.san1ch.vocabanana.feature.text.domain.model.TextListPreview
import com.san1ch.vocabanana.feature.text.presentation.model.GenerateWordsFromTextUiState
import com.san1ch.vocabanana.feature.text.presentation.model.TextWithContent
import com.san1ch.vocabanana.feature.text.presentation.textlist.screen.TextListScreenPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextListScreenViewModel @Inject constructor(
    private val coordinator: TextListCoordinator,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(TextListUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<TextListUiEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        onIntent(TextListUiIntent.Management.ObserveTextPreviews)
    }

    fun onIntent(intent: TextListUiIntent) {
        coordinator.handleIntent(
            intent = intent,
            scope = viewModelScope,
            state = _uiState.value,
            updateState = _uiState::update,
            sendEvent = { sendEvent(it) },
            sendEffect = { effect ->
                viewModelScope.launch {
                    _effect.emit(effect)
                }
            },
            currentState = _uiState.value,
        )
    }
}

sealed class TextListUiIntent {
    sealed class Navigation : TextListUiIntent() {
        object NavigateToAddText : Navigation()
        data class PageChanged(val page: TextListScreenPage) : Navigation()
        object ShowRenderSettings : Navigation()
        object CloseReaderSettings : Navigation()
    }

    sealed class Reader : TextListUiIntent() {
        data class SelectText(val id: Int) : Reader()
        data class UpdateProgress(val id: Int, val progress: Float) : Reader()
        object ToggleLock : Reader()
        object NotifySwipeBlocked : Reader()
        object ResetSwipeAttempt : Reader()
        data class ChangeWordStates(val states: Set<WordState>) : Reader()
        data class ChangePageSettings(val settings: TextAppearanceSettings) : Reader()
        data class ChangeWordState(val id: Int, val state: WordState) : Reader()
    }

    sealed class Dictionary : TextListUiIntent() {
        data class WordClicked(val word: String) : Dictionary()
        object CloseWordInfo : Dictionary()
        data class OxfordMoreInfo(val word: String) : Dictionary()
        data class GenerateWords(val textId: Int) : Dictionary()
        object ClearIdToGenerateWords : Dictionary()
    }

    sealed class Management : TextListUiIntent() {
        object ObserveTextPreviews : Management()
        data class SelectTextToDelete(val textId: Int) : Management()
        object ClearTextIdToDelete : Management()
        object DeleteText : Management()
    }
}

data class TextListUiState(
    // --- Navigation & Global Layout ---
    val currentPage: TextListScreenPage = TextListScreenPage.MyTexts,
    val showSettings: Boolean = false,
    val isLockedByReaderLocker: Boolean = false,

    val currentIdToGenerateWords: Int? = null,

    // --- Content (The "List" and the "Reader") ---
    val textItems: List<TextListPreview> = emptyList(),
    val selectedText: Resource<TextWithContent> = Resource.Empty,

    // --- Word & Dictionary Logic ---
    val showWordInfoState: WordInfoState = WordInfoState.Hidden,
    val generatingState: GenerateWordsFromTextUiState? = null,

    // --- Operations & Feedback (Temporary States) ---
    val selectedTextIdToDelete: Int? = null,
    val isSwipeAttempted: Boolean = false,
)

sealed interface TextListUiEffect {
    data object NavigateToReader : TextListUiEffect
}

sealed class WordInfoState {
    object Hidden : WordInfoState()
    object Loading : WordInfoState()
    data class Found(val word: WordUi, val wordCountInTheText: TextWordCount) : WordInfoState()
    data class NotFound(val word: String) : WordInfoState()
}
