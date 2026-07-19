package com.san1ch.vocabanana.feature.text.presentation.textlist

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.ui.BaseViewModel
import com.san1ch.vocabanana.core.ui.model.WordUi
import com.san1ch.vocabanana.feature.text.domain.model.TextListItem
import com.san1ch.vocabanana.feature.text.domain.model.TextListPreview
import com.san1ch.vocabanana.feature.text.presentation.data.TextToken
import com.san1ch.vocabanana.feature.text.presentation.model.GenerateWordsFromTextUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TextListScreenViewModel @Inject constructor(
    private val coordinator: TextListCoordinator,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(TextListUiState())
    val uiState = _uiState.asStateFlow()

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
        )
    }
}

sealed class TextListUiIntent {
    sealed class Navigation : TextListUiIntent() {
        object NavigateToAddText : Navigation()
        data class PageChanged(val page: Int) : Navigation()
        object ShowRenderSettings : Navigation()
        object CloseReaderSettings : Navigation()
    }

    sealed class Reader : TextListUiIntent() {
        data class SelectText(val id: Int) : Reader()
        object ClearSelection : Reader()
        data class UpdateProgress(val id: Int, val progress: Float) : Reader()
        object ToggleLock : Reader()
        object NotifySwipeBlocked : Reader()
        object ResetSwipeAttempt : Reader()
        data class ChangeWordStates(val states: Set<WordState>) : Reader()
        data class ChangePageSettings(val settings: TextAppearanceSettings) : Reader()
    }

    sealed class Dictionary : TextListUiIntent() {
        data class WordClicked(val word: String) : Dictionary()
        object CloseWordInfo : Dictionary()
        data class OxfordMoreInfo(val word: String) : Dictionary()
        object GenerateWords : Dictionary()
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
    val pagerPage: Int = 0,
    val showSettings: Boolean = false,
    val isLockedByReaderLocker: Boolean = false,

    // --- Content (The "List" and the "Reader") ---
    val textItems: List<TextListPreview> = emptyList(),
    val selectedText: TextListItem? = null,
    val textContent: List<List<TextToken>> = emptyList(),

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
