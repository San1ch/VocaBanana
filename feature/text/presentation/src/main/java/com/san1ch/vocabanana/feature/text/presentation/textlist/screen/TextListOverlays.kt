package com.san1ch.vocabanana.feature.text.presentation.textlist.screen

import androidx.compose.runtime.Composable
import com.san1ch.vocabanana.core.ui.compose.DeleteConfirmDialog
import com.san1ch.vocabanana.core.ui.state.ResourceObserver
import com.san1ch.vocabanana.feature.text.presentation.textlist.page.reading.ReaderSettingsPanel
import com.san1ch.vocabanana.feature.text.presentation.textlist.page.reading.WordInfoPopup
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiState

@Composable
fun TextListOverlays(
    state: TextListUiState,
    onIntent: (TextListUiIntent) -> Unit,
) {
    DeleteConfirmDialog(
        item = state.selectedTextIdToDelete,
        onDismiss = { onIntent(TextListUiIntent.Management.ClearTextIdToDelete) },
        onConfirm = { _ ->
            onIntent(TextListUiIntent.Management.DeleteText)
        },
    )

    WordInfoPopup(
        state = state.wordInfoState,
        onDismiss = { onIntent(TextListUiIntent.Dictionary.CloseWordInfo) },
        onOxfordClick = { onIntent(TextListUiIntent.Dictionary.OxfordMoreInfo(it)) },
    )

    ResourceObserver(
        state = state.selectedText,
        onEmpty = { },
        onLoading = { },
        onError = { },
    ) { text ->
        ReaderSettingsPanel(
            visibility = state.showSettings,
            text = text,
            onIntent = onIntent,
            onStatesSave = { onIntent(TextListUiIntent.Reader.ChangeWordStates(it)) },
        )
    }
}
