package com.san1ch.vocabanana.feature.text.presentation.textlist.textlistscreenpages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.ui.compose.AnimatedTitle
import com.san1ch.vocabanana.core.ui.compose.DeleteConfirmDialog
import com.san1ch.vocabanana.feature.text.presentation.R
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiState
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TextListContent(
    state: TextListUiState,
    onIntent: (TextListUiIntent) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { TextListScreenPage.entries.size })
    val coroutineScope = rememberCoroutineScope()

    val currentPage = remember(pagerState.currentPage) {
        TextListScreenPage.fromIndex(pagerState.currentPage)
    }

    BackHandler(enabled = pagerState.currentPage != 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onIntent(TextListUiIntent.Navigation.PageChanged(pagerState.currentPage))
    }

    SetupSwipeLockEffects(state.isSwipeAttempted) {
        onIntent(TextListUiIntent.Reader.ResetSwipeAttempt)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CustomTouchSlopProvider {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            val title = when (currentPage) {
                                TextListScreenPage.MyTexts -> "My Texts"
                                TextListScreenPage.TextReader ->
                                    state.selectedText?.title
                                        ?: "Loading..."

                                TextListScreenPage.Settings -> "Settings"
                            }
                            AnimatedTitle(title)
                        },
                        actions = {
                            TopBarActions(
                                currentPage = state.pagerPage,
                                isLocked = state.isLocked,
                                isSwipeAttempted = state.isSwipeAttempted,
                                onLockClick = { onIntent(TextListUiIntent.Reader.ToggleLock) },
                                onPageSettings = {
                                    onIntent(TextListUiIntent.Navigation.ShowRenderSettings)
                                },
                            )
                        },
                    )
                },
                floatingActionButton = {
                    FabAnimated(
                        visible = state.pagerPage == 0,
                        onClick = { onIntent(TextListUiIntent.Navigation.NavigateToAddText) },
                    )
                },
            ) { paddingValues ->
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .padding(paddingValues)
                        .pointerInput(state.isLocked) {
                            if (state.isLocked) {
                                detectDragGestures { change, dragAmount ->
                                    if (abs(dragAmount.x) > abs(dragAmount.y)) {
                                        onIntent(TextListUiIntent.Reader.NotifySwipeBlocked)
                                        change.consume()
                                    }
                                }
                            }
                        },
                    userScrollEnabled = !state.isLocked && (pagerState.currentPage == 0 || state.selectedText != null),
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
                            TextReaderPage(state.selectedText, state.textContent, onIntent)

                        TextListScreenPage.Settings ->
                            TextSettingsPage(state.generatingState, onIntent)
                    }
                }
            }
        }

        DeleteConfirmDialog(
            item = state.selectedTextIdToDelete,
            onDismiss = { onIntent(TextListUiIntent.Management.ClearTextIdToDelete) },
            onConfirm = { text ->
                onIntent(TextListUiIntent.Management.DeleteText)
            },
        )

        WordInfoPopup(
            state = state.wordInfoState,
            onDismiss = { onIntent(TextListUiIntent.Dictionary.CloseWordInfo) },
            onOxfordClick = { onIntent(TextListUiIntent.Dictionary.OxfordMoreInfo(it)) },
        )

        ReaderSettingsPanel(
            visibility = state.showSettings,
            settings = state.selectedText?.textAppearanceSettings ?: TextAppearanceSettings(),
            onIntent = onIntent,
        )
    }
}

@Composable
private fun TopBarActions(
    currentPage: Int,
    isLocked: Boolean,
    isSwipeAttempted: Boolean,
    onLockClick: () -> Unit,
    onPageSettings: () -> Unit,
) {
    Row {
        AnimatedVisibility(visible = currentPage == 1, enter = fadeIn(), exit = fadeOut()) {
            IconButton(onClick = onLockClick) {
                AnimatedLockIcon(isLocked = isLocked, isSwipeAttempted = isSwipeAttempted)
            }
        }
        AnimatedVisibility(visible = currentPage == 1, enter = fadeIn(), exit = fadeOut()) {
            IconButton(onClick = onPageSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    }
}

@Composable
private fun AnimatedLockIcon(isLocked: Boolean, isSwipeAttempted: Boolean) {
    val targetColor = when {
        isSwipeAttempted -> Color.Red
        isLocked -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        label = stringResource(R.string.lockcolor),
    )
    val iconPainter =
        rememberVectorPainter(if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen)

    val scale by animateFloatAsState(
        targetValue = if (isSwipeAttempted) 1.5f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
    )
    Spacer(
        modifier = Modifier
            .size(24.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawWithCache {
                onDrawBehind {
                    with(iconPainter) {
                        draw(
                            size = size,
                            colorFilter = ColorFilter.tint(animatedColor),
                        )
                    }
                }
            },
    )
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
private fun CustomTouchSlopProvider(content: @Composable () -> Unit) {
    val viewConfiguration = LocalViewConfiguration.current
    val customViewConfiguration = remember {
        object : ViewConfiguration by viewConfiguration {
            override val touchSlop: Float get() = viewConfiguration.touchSlop * 10f
        }
    }
    CompositionLocalProvider(LocalViewConfiguration provides customViewConfiguration) {
        content()
    }
}

@Composable
private fun FabAnimated(visible: Boolean, onClick: () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        FloatingActionButton(onClick = onClick) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}
