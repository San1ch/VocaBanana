package com.san1ch.vocabanana.feature.vocabulary.presentation.vocabularyscreenpages

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
import com.san1ch.vocabanana.core.ui.state.ResourceObserver
import com.san1ch.vocabanana.core.ui.compose.CollectResource
import com.san1ch.vocabanana.feature.vocabulary.presentation.VocabularyIntent
import com.san1ch.vocabanana.feature.vocabulary.presentation.VocabularyScreenViewModel
import kotlinx.coroutines.launch


@Composable
fun VocabularyScreen(
    viewModel: VocabularyScreenViewModel = hiltViewModel()
) {
    CollectResource(viewModel.events)

    // Single observation point
    val state by viewModel.resource.collectAsState()

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

    BackHandler(enabled = pagerState.currentPage != 0 || state.selectedWordId != null) {
        if (pagerState.currentPage != 0) {
            viewModel.onIntent(VocabularyIntent.DeselectWord)
        } else {
            viewModel.onIntent(VocabularyIntent.NavigateBack)
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
        ResourceObserver(state.wordsState) { words ->
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
                        onUpdateWord = { viewModel.onIntent(VocabularyIntent.UpdateWord(it)) }
                    )
                }
            }
        }
    }
}

