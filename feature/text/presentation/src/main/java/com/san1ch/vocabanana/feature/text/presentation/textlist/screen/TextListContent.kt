package com.san1ch.vocabanana.feature.text.presentation.textlist.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.san1ch.vocabanana.core.ui.state.Resource
import com.san1ch.vocabanana.core.ui.state.ResourceObserver
import com.san1ch.vocabanana.feature.text.presentation.textlist.page.list.TextListPage
import com.san1ch.vocabanana.feature.text.presentation.textlist.page.reading.TextReaderPage
import com.san1ch.vocabanana.feature.text.presentation.textlist.page.settings.TextSettingsPage
import com.san1ch.vocabanana.feature.text.presentation.textlist.screen.scaffold.TextListTopBar
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

enum class TextListScreenPage(val index: Int) {
    MyTexts(0),
    TextReader(1),
    Settings(2),
    ;

    companion object {
        fun fromIndex(index: Int) = when (index) {
            0 -> MyTexts
            1 -> TextReader
            else -> Settings
        }
    }
}

fun Int.toScreenPage() = TextListScreenPage.fromIndex(this)

// CompositionLocal to track if the pager is scrolling
val LocalIsPagerScrolling = compositionLocalOf { false }

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TextListContent(
    state: TextListUiState,
    onIntent: (TextListUiIntent) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { TextListScreenPage.entries.size })
    val coroutineScope = rememberCoroutineScope()

    val canUserScroll = !state.isLockedByReaderLocker &&
        (
            pagerState.currentPage != TextListScreenPage.TextReader.index ||
                state.selectedText is Resource.Success
            )

    // Back handler for swiping back to previous page
    BackHandler(enabled = pagerState.currentPage != TextListScreenPage.TextReader.index) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }
    LaunchedEffect(pagerState.settledPage) {
        onIntent(TextListUiIntent.Navigation.PageChanged(pagerState.settledPage.toScreenPage()))
    }
    SetupSwipeLockEffects(state.isSwipeAttempted) {
        onIntent(TextListUiIntent.Reader.ResetSwipeAttempt)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalIsPagerScrolling provides pagerState.isScrollInProgress) {
            Scaffold(
                topBar = {
                    TextListTopBar(
                        state,
                        onIntent,
                    )
                },
                floatingActionButton = {
                    val isTextReaderPage =
                        state.currentPage == TextListScreenPage.MyTexts && !LocalIsPagerScrolling.current
                    FabAnimated(
                        isVisible = isTextReaderPage,
                        isClickable = isTextReaderPage,
                        onClick = { onIntent(TextListUiIntent.Navigation.NavigateToAddText) },
                    )
                },
            ) { paddingValues ->
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .padding(paddingValues)
                        .readerSwipeLock(
                            isLocked = state.isLockedByReaderLocker,
                            onSwipeBlocked = { onIntent(TextListUiIntent.Reader.NotifySwipeBlocked) },
                            onReset = { onIntent(TextListUiIntent.Reader.ResetSwipeAttempt) },
                        ),
                    userScrollEnabled = canUserScroll,
                ) { pageIndex ->
                    when (TextListScreenPage.fromIndex(pageIndex)) {
                        TextListScreenPage.MyTexts ->
                            TextListPage(
                                items = state.textItems,
                                onIntent = onIntent,
                                onNavigateToReader = {
                                    coroutineScope.launch { pagerState.animateScrollToPage(1) }
                                },
                            )

                        TextListScreenPage.TextReader ->
                            ResourceObserver(state.selectedText) { text ->
                                TextReaderPage(
                                    text,
                                    onIntent,
                                )
                            }

                        TextListScreenPage.Settings ->
                            TextSettingsPage(state.generatingState, onIntent)
                    }
                }
            }
        }

        TextListOverlays(state, onIntent)
    }
}

private fun Modifier.readerSwipeLock(
    isLocked: Boolean,
    onSwipeBlocked: () -> Unit,
    onReset: () -> Unit,
): Modifier = this.pointerInput(isLocked) {
    if (isLocked) {
        detectDragGestures(
            onDragEnd = onReset,
            onDragCancel = onReset,
        ) { change, dragAmount ->
            if (abs(dragAmount.x) > abs(dragAmount.y)) {
                onSwipeBlocked()
                change.consume()
            }
        }
    }
}

@Composable
private fun SetupSwipeLockEffects(isSwipeAttempted: Boolean, onReset: () -> Unit) {
    LaunchedEffect(isSwipeAttempted) {
        if (isSwipeAttempted) {
            delay(300)
            onReset()
        }
    }
}

@Composable
private fun FabAnimated(isVisible: Boolean, isClickable: Boolean = true, onClick: () -> Unit) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        FloatingActionButton(onClick = {
            if (isClickable) onClick()
        }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}
