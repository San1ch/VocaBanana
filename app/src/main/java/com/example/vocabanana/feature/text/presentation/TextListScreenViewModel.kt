package com.example.vocabanana.feature.text.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.asUiState
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.presentation.UiText
import com.example.vocabanana.feature.text.domain.GenerateWordsFromTextState
import com.example.vocabanana.feature.text.domain.GenerateWordsFromTextUseCase
import com.example.vocabanana.feature.text.presentation.data.GenerateWordsFromTextUiState
import com.example.vocabanana.feature.text.presentation.data.TextUi
import com.example.vocabanana.feature.text.presentation.data.toPreview
import com.example.vocabanana.feature.text.presentation.data.toUi
import com.example.vocabanana.feature.text.presentation.data.toUiState
import com.example.vocabanana.feature.text.presentation.data.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextListScreenViewModel @Inject constructor(
    private val textRepository: TextRepository,
    private val wordRepository: WordRepository,
    private val generateWordsFromText: GenerateWordsFromTextUseCase
) : BaseViewModel() {

    private var saveJob: Job? = null
    private val _generatingStateMessage =
        MutableStateFlow<GenerateWordsFromTextUiState?>(null)
    val generatingState = _generatingStateMessage.asStateFlow()


    val textPreviews = textRepository.getTexts()
        .map { list -> list.map { it.toPreview() } }
        .asUiState()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            UiState.Loading
        )

    private val _currentText = MutableStateFlow<TextUi?>(null)
    val currentText = _currentText.asStateFlow()

    private val _wordInfoState = MutableStateFlow<WordInfoState>(WordInfoState.Hidden)
    val wordInfoState = _wordInfoState.asStateFlow()

    fun selectWordInPage(word: String) {
        val cleanWord = word.trim().lowercase()
        _wordInfoState.value = WordInfoState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            // Replace this with your actual DB call
            val wordDomain = wordRepository.getWordByWord(cleanWord)

            if (wordDomain != null) {
                _wordInfoState.value = WordInfoState.Found(wordDomain.toUi())
            } else {
                _wordInfoState.value = WordInfoState.NotFound(cleanWord)
            }
        }
    }

    fun closeWordInfo() {
        _wordInfoState.value = WordInfoState.Hidden
    }

    fun selectText(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentText.value = textRepository.getTextById(id).toUi()
        }
    }

    fun generateWords() {
        val textId = _currentText.value?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            generateWordsFromText(textId).collect {
                _generatingStateMessage.value = it.toUiState()
            }
        }
    }

    fun resetGenerateState() {
        _generatingStateMessage.value = null
    }

    fun clearSelection() {
        _currentText.value = null
    }


    fun updateProgress(textId: Int, position: Float) {
        saveJob?.cancel()
        saveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            textRepository.updateProgress(textId, position, System.currentTimeMillis())
        }
    }


    fun deleteText(textId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            textRepository.deleteText(textId)
        }
    }
}