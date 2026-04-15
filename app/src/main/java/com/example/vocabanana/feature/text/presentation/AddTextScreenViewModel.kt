package com.example.vocabanana.feature.text.presentation

import androidx.lifecycle.viewModelScope
import com.example.vocabanana.core.presentation.BaseViewModel
import com.example.vocabanana.core.presentation.UiEvent
import com.example.vocabanana.feature.text.domain.model.TextValidateError
import com.example.vocabanana.feature.text.domain.usecase.CreateTextUseCase
import com.example.vocabanana.feature.text.presentation.data.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTextScreenViewModel @Inject constructor(
    private val createTextUseCase: CreateTextUseCase,
) : BaseViewModel() {

    private var selectedUri: android.net.Uri? = null

    fun setFileUri(uri: android.net.Uri?) {
        selectedUri = uri
    }

    fun addText(title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val error = createTextUseCase(title, content)) {
                TextValidateError.EmptyText ->
                    sendEvent(UiEvent.ShowToast(error.toUiText()))

                is TextValidateError.InvalidName ->
                    sendEvent(UiEvent.ShowToast(error.toUiText()))

                TextValidateError.NameAlreadyExists ->
                    sendEvent(UiEvent.ShowToast(error.toUiText()))

                is TextValidateError.TooLongName ->
                    sendEvent(UiEvent.ShowToast(error.toUiText()))

                null -> sendEvent(UiEvent.NavigateBack)
            }
        }
    }
}