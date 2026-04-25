@file:Suppress("DEPRECATION")

package com.example.vocabanana.feature.text.presentation.textlistscreenpages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.feature.text.presentation.TextListScreenViewModel
import com.example.vocabanana.ui.composable.CollectUiEvents


@Composable
fun TextListScreen(
    viewModel: TextListScreenViewModel = hiltViewModel(),
    navigateTo: (AppDestination) -> Unit,
    navigateBack: () -> Unit,
) {
    CollectUiEvents(
        events = viewModel.events,
        navigateBack = navigateBack,
        navigateTo = { navigateTo(it) })


    val state by viewModel.uiState.collectAsState()

    TextListContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}
