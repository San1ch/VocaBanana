package com.san1ch.vocabanana.feature.text.presentation

import androidx.lifecycle.viewModelScope
import com.san1ch.vocabanana.core.essentials.model.constants.TextConstant
import com.san1ch.vocabanana.core.essentials.resources.CoreStringProvider
import com.san1ch.vocabanana.core.ui.BaseViewModel
import com.san1ch.vocabanana.core.ui.UiEvent
import com.san1ch.vocabanana.feature.text.domain.usecase.CreateTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTextScreenViewModel @Inject constructor(
    private val createTextUseCase: CreateTextUseCase,
    private val addTextRouter: AddTextRouter,
    private val stringProvider: CoreStringProvider
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AddTextUiState())
    val uiState = _uiState.asStateFlow()

    fun onIntent(intent: AddTextUiIntent) {
        when (intent) {
            is AddTextUiIntent.TitleChanged -> updateTitle(intent.title)
            is AddTextUiIntent.ContentChanged -> updateContent(intent.content)
            is AddTextUiIntent.FileLoaded -> handleFileLoaded(intent.fileName, intent.content)
            is AddTextUiIntent.StartLoadingFile -> _uiState.update { it.copy(isLoadingFile = true) }
            is AddTextUiIntent.ClearClicked -> clearFields()
            is AddTextUiIntent.AddTextClicked -> submitText()
            is AddTextUiIntent.BackClicked -> addTextRouter.navigateBack()
        }
    }

    private fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(
                title = newTitle,
                isTitleTooLong = newTitle.length > TextConstant.MAX_NAME_LENGTH
            )
        }
    }

    private fun updateContent(newContent: String) {
        _uiState.update {
            it.copy(
                content = newContent,
                fileName = if (newContent.isNotBlank()) null else it.fileName
            )
        }
    }

    private fun handleFileLoaded(name: String, fileContent: String) {
        _uiState.update {
            it.copy(
                content = fileContent,
                fileName = name,
                isLoadingFile = false
            )
        }
    }

    private fun clearFields() {
        _uiState.update { it.copy(title = "", content = "", fileName = null) }
    }

    private fun submitText() {
        val currentState = _uiState.value
        if (currentState.content.isBlank() || currentState.isTitleTooLong) return

        viewModelScope.launch(Dispatchers.IO) {
            createTextUseCase(currentState.title, currentState.content).fold(  onSuccess = {
                addTextRouter.navigateBack()
            },onFailure = { error ->
                sendEvent(UiEvent.ShowToast(error.message ?: stringProvider.unknownErrorMessage))
            })

        }
    }
}

data class AddTextUiState(
    val title: String = "",
    val content: String = "",
    val fileName: String? = null,
    val isLoadingFile: Boolean = false,
    val isTitleTooLong: Boolean = false
)

sealed class AddTextUiIntent {
    data class TitleChanged(val title: String) : AddTextUiIntent()
    data class ContentChanged(val content: String) : AddTextUiIntent()
    object StartLoadingFile : AddTextUiIntent()
    data class FileLoaded(val fileName: String, val content: String) : AddTextUiIntent()

    object ClearClicked : AddTextUiIntent()
    object AddTextClicked : AddTextUiIntent()
    object BackClicked : AddTextUiIntent()
}