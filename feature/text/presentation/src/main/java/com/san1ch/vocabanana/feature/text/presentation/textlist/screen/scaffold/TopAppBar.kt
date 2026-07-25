package com.san1ch.vocabanana.feature.text.presentation.textlist.screen.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.san1ch.vocabanana.core.ui.compose.AnimatedLockIconButton
import com.san1ch.vocabanana.core.ui.compose.AnimatedTitle
import com.san1ch.vocabanana.core.ui.state.fold
import com.san1ch.vocabanana.feature.text.presentation.textlist.screen.LocalIsPagerScrolling
import com.san1ch.vocabanana.feature.text.presentation.textlist.screen.TextListScreenPage
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextListTopBar(
    state: TextListUiState,
    onIntent: (TextListUiIntent) -> Unit,
) {
    TopAppBar(
        title = {
            val title = when (state.currentPage) {
                TextListScreenPage.MyTexts -> "My Texts"
                TextListScreenPage.TextReader ->
                    state.selectedText.fold(
                        onLoading = { "Loading..." },
                        onEmpty = { "No Text Selected" },
                        onSuccess = { it.text.title },
                        onError = { "Error" },
                    )
                // TODO: Change to localized method
            }
            AnimatedTitle(title)
        },
        actions = {
            TextListTopBarActions(
                currentPage = state.currentPage,
                isLocked = state.isLockedByReaderLocker,
                isSwipeAttempted = state.isSwipeAttempted,
                onLockClick = { onIntent(TextListUiIntent.Reader.ToggleLock) },
                onPageSettings = {
                    onIntent(TextListUiIntent.Navigation.ShowRenderSettings)
                },
            )
        },
    )
}

@Composable
private fun TextListTopBarActions(
    currentPage: TextListScreenPage,
    isLocked: Boolean,
    isSwipeAttempted: Boolean,
    onLockClick: () -> Unit,
    onPageSettings: () -> Unit,
) {
    val isTextReaderPage = currentPage == TextListScreenPage.TextReader && !LocalIsPagerScrolling.current
    Row {
        AnimatedVisibility(visible = isTextReaderPage, enter = fadeIn(), exit = fadeOut()) {
            AnimatedLockIconButton(
                isLocked = isLocked,
                isSwipeAttempted = isSwipeAttempted,
                onLockClick = {
                    if (isTextReaderPage) {
                        onLockClick()
                    }
                },
            )
        }
        AnimatedVisibility(visible = isTextReaderPage, enter = fadeIn(), exit = fadeOut()) {
            IconButton(onClick = {
                if (isTextReaderPage) {
                    onPageSettings()
                }
            }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    }
}
