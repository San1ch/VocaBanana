package com.san1ch.vocabanana.feature.text.presentation.textlist.handler

import com.san1ch.vocabanana.feature.text.presentation.TextListRouter
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiState
import javax.inject.Inject

class TextListNavigationHandler @Inject constructor(
    private val router: TextListRouter,
) {

    fun handle(
        intent: TextListUiIntent.Navigation,
        updateState: ((TextListUiState) -> TextListUiState) -> Unit,
    ) {
        when (intent) {
            TextListUiIntent.Navigation.NavigateToAddText -> {
                router.navigateToAddText()
            }

            is TextListUiIntent.Navigation.PageChanged -> {
                updateState {
                    it.copy(
                        pagerPage = intent.page
                    )
                }
            }

            TextListUiIntent.Navigation.ShowRenderSettings -> {
                updateState {
                    it.copy(
                        showSettings = true
                    )
                }
            }

            TextListUiIntent.Navigation.CloseReaderSettings -> {
                updateState {
                    it.copy(
                        showSettings = false
                    )
                }
            }
        }
    }
}