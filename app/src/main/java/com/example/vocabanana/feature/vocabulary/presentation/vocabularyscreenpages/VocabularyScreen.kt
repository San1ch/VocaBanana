package com.example.vocabanana.feature.vocabulary.presentation.vocabularyscreenpages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.feature.vocabulary.presentation.VocabularyIntent
import com.example.vocabanana.feature.vocabulary.presentation.VocabularyScreenViewModel
import com.example.vocabanana.ui.composable.CollectUiEvents
import kotlinx.coroutines.launch


@Composable
fun VocabularyScreen(
    viewModel: VocabularyScreenViewModel = hiltViewModel(),
    navigateTo: (AppDestination) -> Unit,
    navigateBack: () -> Unit
) {
    CollectUiEvents(viewModel.events, navigateBack, navigateTo)

    // Single observation point
    val state by viewModel.uiState.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(state.selectedWordId) {
        if (state.selectedWordId != null) {
            pagerState.animateScrollToPage(1)
        } else {
            pagerState.animateScrollToPage(0)
        }
    }

    val onBackToList = {
        viewModel.onIntent(VocabularyIntent.DeselectWord)
    }

    BackHandler(enabled = pagerState.currentPage != 0 || state.selectedWordId != null) {
        if (pagerState.currentPage != 0) {
            onBackToList()
        } else {
            navigateBack()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            VocabularyDrawerContent(
                wordFilter = state.wordFilter,
                onIntent = viewModel::onIntent,
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        // Observe the wordsState inside the main UI
        StateObserver(state.wordsState) { words ->
            val selectedWord = words.find { it.id == state.selectedWordId }

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = selectedWord != null,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> VocabularyListPage(
                        words = words,
                        stats = state.stats,
                        newWordsCount = state.newWordsCount,
                        wordFilter = state.wordFilter,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onIntent = { intent ->
                            viewModel.onIntent(intent)
                            if (intent is VocabularyIntent.SelectWord) {
                                scope.launch { pagerState.animateScrollToPage(1) }
                            }
                        }
                    )

                    1 -> WordDetailsAndEditPage(
                        word = selectedWord,
                        onBack = onBackToList,
                        onUpdateWord = { viewModel.onIntent(VocabularyIntent.UpdateWord(it)) }
                    )
                }
            }
        }
    }
}

