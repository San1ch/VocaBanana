package com.san1ch.vocabanana.feature.text.presentation.textlist.handler


import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.feature.text.domain.usecase.GetTextPreviewsUseCase
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class TextListManagementHandler @Inject constructor(
    private val textRepository: TextRepository,
    private val getTextPreviewsUseCase: GetTextPreviewsUseCase,
) {

    fun handle(
        intent: TextListUiIntent.Management,
        scope: CoroutineScope,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        when (intent) {
            TextListUiIntent.Management.ObserveTextPreviews -> {
                observeTextPreviews(
                    scope = scope,
                    updateState = updateState
                )
            }

            is TextListUiIntent.Management.SelectTextToDelete -> {
                updateState {
                    it.copy(
                        selectedTextIdToDelete = intent.textId
                    )
                }
            }

            TextListUiIntent.Management.ClearTextIdToDelete -> {
                updateState {
                    it.copy(
                        selectedTextIdToDelete = null
                    )
                }
            }

            TextListUiIntent.Management.DeleteText -> {
                deleteText(
                    scope = scope,
                    updateState = updateState
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
                        textItems = textItems
                    )
                }
            }
        }
    }

    private fun deleteText(
        scope: CoroutineScope,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        var idToDelete: Int? = null

        updateState { state ->
            idToDelete = state.selectedTextIdToDelete
            state
        }

        val id = idToDelete ?: return

        scope.launch {
            textRepository.deleteTexts(listOf(id))

            updateState {
                it.copy(
                    selectedTextIdToDelete = null
                )
            }
        }
    }
}