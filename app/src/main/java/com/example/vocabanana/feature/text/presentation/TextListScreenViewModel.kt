package com.example.vocabanana.feature.text.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabanana.feature.text.domain.TextRepository
import com.example.vocabanana.core.presentation.uistate.UiState
import com.example.vocabanana.core.presentation.uistate.UiStateError
import com.example.vocabanana.core.presentation.uistate.toUiText
import com.example.vocabanana.feature.text.presentation.data.TextPreview
import com.example.vocabanana.feature.text.presentation.data.TextUi
import com.example.vocabanana.feature.text.presentation.data.toPreview
import com.example.vocabanana.feature.text.presentation.data.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextListScreenViewModel @Inject constructor(
    private val textRepository: TextRepository,
) : ViewModel() {
    private var saveJob: Job? = null

    val textPreviews =
        textRepository.getTexts()
            .map { list ->
                UiState.Success(list.map { it.toPreview() }) as UiState<List<TextPreview>>
            }
            .catch { e ->
                emit(UiState.Error(UiStateError.Unknown(e.message ?: "Unknown error").toUiText()))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                UiState.Loading
            )


    private val _currentText = MutableStateFlow<TextUi?>(null)
    val currentText = _currentText.asStateFlow()

    fun selectText(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentText.value = textRepository.getTextById(id).toUi()
        }
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
}

