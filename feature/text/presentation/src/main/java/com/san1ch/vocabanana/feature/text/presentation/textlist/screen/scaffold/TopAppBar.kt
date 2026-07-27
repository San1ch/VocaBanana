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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.san1ch.vocabanana.core.ui.compose.AnimatedLockIconButton
import com.san1ch.vocabanana.core.ui.compose.AnimatedTitle
import com.san1ch.vocabanana.core.ui.state.Resource
import com.san1ch.vocabanana.core.ui.state.fold
import com.san1ch.vocabanana.feature.text.presentation.R
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
                TextListScreenPage.MyTexts -> stringResource(R.string.my_texts_title)
                TextListScreenPage.TextReader -> when (val textState = state.selectedText) {
                    is Resource.Loading -> stringResource(R.string.loading_title)
                    is Resource.Empty -> stringResource(R.string.no_text_selected_title)
                    is Resource.Success -> textState.data.text.title
                    is Resource.Error -> stringResource(R.string.error_title)
                }
            }
            AnimatedTitle(title)
        },
        actions = {
            TextListTopBarActions(
                currentPage = state.currentPage,
                isLocked = state.isLockedByReaderLocker,
                isSwipeAttempted = state.isSwipeAttempted,
                onLockClick = { onIntent(TextListUiIntent.Reader.ToggleLock) },
                onPageSettings = { onIntent(TextListUiIntent.Navigation.ShowRenderSettings) },
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
