package com.example.vocabanana.feature.text.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabanana.feature.text.domain.TextRepository
import com.example.vocabanana.ui.data.UiState
import com.example.vocabanana.ui.data.UiStateError
import com.example.vocabanana.ui.data.mapper.GetTextsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
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
    val textPreviews =
        textRepository.getTexts()
            .map { list ->
                UiState.Success(list.map { it.toPreview() }) as UiState<List<TextPreview>>
            }
            .catch { e ->
                emit(UiState.Error(UiStateError.Unknown))
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
}

data class TextUi(val id: Int, val title: String, val content: String)