package com.san1ch.vocabanana.feature.text.presentation.textlist.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.san1ch.vocabanana.core.ui.compose.CollectUiEvents
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListScreenViewModel

@Composable
fun TextListScreen(
    viewModel: TextListScreenViewModel = hiltViewModel(),
) {
    CollectUiEvents(viewModel.events)

    val state by viewModel.uiState.collectAsState()
    TextListContent(
        state = state,
        effectFlow = viewModel.effect,
        onIntent = viewModel::onIntent,
    )
}
