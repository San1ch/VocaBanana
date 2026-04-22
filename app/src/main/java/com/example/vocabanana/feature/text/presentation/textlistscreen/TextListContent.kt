package com.example.vocabanana.feature.text.presentation.textlistscreen

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import com.example.vocabanana.feature.text.presentation.TextListUiIntent
import com.example.vocabanana.feature.text.presentation.TextListUiState
import com.example.vocabanana.ui.composable.AnimatedTitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TextListContent(
    state: TextListUiState,
    onIntent: (TextListUiIntent) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // Sync Pager with State
    LaunchedEffect(pagerState.currentPage) {
        onIntent(TextListUiIntent.PageChanged(pagerState.currentPage))
    }

    // Effect for the "Red Flash" on the lock
    SetupSwipeLockEffects(state.isSwipeAttempted) {
        onIntent(TextListUiIntent.ResetSwipeAttempt)
    }

    CustomTouchSlopProvider {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        val title = when (state.pagerPage) {
                            0 -> "My Texts"
                            1 -> state.selectedText?.title ?: "Loading..."
                            else -> "Settings"
                        }
                        AnimatedTitle(title)
                    },
                    actions = {
                        TopBarActions(
                            currentPage = state.pagerPage,
                            isLocked = state.isLocked,
                            isSwipeAttempted = state.isSwipeAttempted,
                            onLockClick = { onIntent(TextListUiIntent.ToggleLock) },
                            onSettingsClick = {
                                if (state.isLocked) onIntent(TextListUiIntent.NotifySwipeBlocked)
                                else coroutineScope.launch { pagerState.animateScrollToPage(2) }
                            }
                        )
                    }
                )
            },
            floatingActionButton = {
                FabAnimated(
                    visible = state.pagerPage == 0,
                    onClick = { onIntent(TextListUiIntent.NavigateToAddText) }
                )
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(paddingValues)
                    .pointerInput(state.isLocked) {
                        if (state.isLocked) detectDragGestures { change, dragAmount ->
                            if (abs(dragAmount.x) > abs(dragAmount.y)) {
                                onIntent(TextListUiIntent.NotifySwipeBlocked)
                                change.consume()
                            }
                        }
                    },
                userScrollEnabled = state.selectedText != null && !state.isLocked
            ) { page ->
                when (page) {
                    0 -> TextListPage(
                        items = state.textItems,
                        onIntent = onIntent,
                        onNavigateToReader = {
                            coroutineScope.launch { pagerState.animateScrollToPage(1) }
                        })

                    1 -> TextReaderPage(state.selectedText, onIntent)
                    2 -> TextSettingsPage(state.generatingState, onIntent)
                }
            }
        }
    }
}

@Composable
private fun TopBarActions(
    currentPage: Int,
    isLocked: Boolean,
    isSwipeAttempted: Boolean,
    onLockClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row {
        AnimatedVisibility(visible = currentPage == 1, enter = fadeIn(), exit = fadeOut()) {
            IconButton(onClick = onLockClick) {
                AnimatedLockIcon(isLocked = isLocked, isSwipeAttempted = isSwipeAttempted)
            }
        }
        AnimatedVisibility(visible = currentPage == 1, enter = fadeIn(), exit = fadeOut()) {
            IconButton(onClick = onSettingsClick) {
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

    val animatedColor by animateColorAsState(targetValue = targetColor, label = "LockColor")
    val iconPainter =
        rememberVectorPainter(if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen)

    val scale by animateFloatAsState(
        targetValue = if (isSwipeAttempted) 1.5f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f)
    )
    Spacer(
        modifier = Modifier
            .size(24.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .drawWithCache {
                onDrawBehind {
                    with(iconPainter) {
                        draw(
                            size = size,
                            colorFilter = ColorFilter.tint(animatedColor)
                        )
                    }
                }
            }
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
        exit = fadeOut() + scaleOut()
    ) {
        FloatingActionButton(onClick = onClick) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}