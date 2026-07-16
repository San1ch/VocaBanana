package com.san1ch.vocabanana.feature.text.presentation.textlist

import com.san1ch.vocabanana.core.ui.model.UiEvent
import com.san1ch.vocabanana.feature.text.presentation.textlist.handler.TextListDictionaryHandler
import com.san1ch.vocabanana.feature.text.presentation.textlist.handler.TextListManagementHandler
import com.san1ch.vocabanana.feature.text.presentation.textlist.handler.TextListNavigationHandler
import com.san1ch.vocabanana.feature.text.presentation.textlist.handler.TextListReaderHandler
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject


class TextListCoordinator @Inject constructor(
    private val navigationHandler: TextListNavigationHandler,
    private val readerHandler: TextListReaderHandler,
    private val dictionaryHandler: TextListDictionaryHandler,
    private val managementHandler: TextListManagementHandler,
) {

    fun handleIntent(
        intent: TextListUiIntent,
        scope: CoroutineScope,
        state: TextListUiState,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
        sendEvent: (UiEvent) -> Unit,
    ) {
        when (intent) {
            is TextListUiIntent.Navigation -> {
                navigationHandler.handle(
                    intent = intent,
                    updateState = updateState
                )
            }

            is TextListUiIntent.Reader -> {
                readerHandler.handle(
                    intent = intent,
                    scope = scope,
                    updateState = updateState
                )
            }

            is TextListUiIntent.Dictionary -> {
                dictionaryHandler.handle(
                    intent = intent,
                    scope = scope,
                    state = state,
                    updateState = updateState,
                    sendEvent = { sendEvent(it) }
                )
            }

            is TextListUiIntent.Management -> {
                managementHandler.handle(
                    intent = intent,
                    scope = scope,
                    updateState = updateState
                )
            }
        }
    }
}