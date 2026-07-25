package com.san1ch.vocabanana.feature.text.presentation.textlist.handler

import com.san1ch.vocabanana.core.essentials.Logger
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.ui.state.Resource
import com.san1ch.vocabanana.feature.text.domain.usecase.GetTextPreviewsUseCase
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TextListManagementHandler @Inject constructor(
    private val textRepository: TextRepository,
    private val getTextPreviewsUseCase: GetTextPreviewsUseCase,
    private val logger: Logger,
) {

    fun handle(
        intent: TextListUiIntent.Management,
        scope: CoroutineScope,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
        currentState: TextListUiState,
    ) {
        when (intent) {
            TextListUiIntent.Management.ObserveTextPreviews -> {
                observeTextPreviews(
                    scope = scope,
                    updateState = updateState,
                )
            }

            is TextListUiIntent.Management.SelectTextToDelete -> {
                updateState {
                    it.copy(
                        selectedTextIdToDelete = intent.textId,
                    )
                }
            }

            TextListUiIntent.Management.ClearTextIdToDelete -> {
                updateState {
                    it.copy(
                        selectedTextIdToDelete = null,
                    )
                }
            }

            TextListUiIntent.Management.DeleteText -> {
                deleteText(
                    scope = scope,
                    updateState = updateState,
                    currentState = currentState,
                )
            }
        }
    }

    private fun observeTextPreviews(
        scope: CoroutineScope,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        scope.launch {
            getTextPreviewsUseCase().collect { textItems ->
                updateState {
                    it.copy(
                        textItems = textItems,
                    )
                }
            }
        }
    }

    private fun deleteText(
        scope: CoroutineScope,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
        currentState: TextListUiState,
    ) {
        val idToDelete: Int = currentState.selectedTextIdToDelete ?: run {
            logger.d("Attempted to delete text, but no text is currently selected.")
            return
        }

        val newSelectedText = when (val resource = currentState.selectedText) {
            is Resource.Success -> {
                if (idToDelete == resource.data.id) {
                    Resource.Empty
                } else {
                    resource
                }
            }

            else -> resource
        }

        scope.launch {
            textRepository.deleteTexts(listOf(idToDelete))

            updateState {
                it.copy(
                    selectedTextIdToDelete = null,
                    selectedText = newSelectedText,
                )
            }
        }
    }
}
