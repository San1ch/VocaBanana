package com.example.vocabanana.feature.text.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.ui.data.ObserveState
import com.example.vocabanana.ui.data.UiState
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun TextListScreen(
    viewModel: TextListScreenViewModel = hiltViewModel(),
    navigateToAddTextScreen: () -> Unit
) {
    val textPreviews by viewModel.textPreviews.collectAsState()

    val currentText by viewModel.currentText.collectAsState()
    val state by viewModel.textPreviews.collectAsState()
    ObserveState(
        state = state
    ) { textPreviews ->
        TextListContent(
            textItems = textPreviews,
            selectedText = currentText,
            onTextSelected = { id ->
                viewModel.selectText(id)
            },
            onClearSelection = {
                viewModel.clearSelection()
            },
            navigateToAddTextScreen = navigateToAddTextScreen
        )
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TextListContent(
    textItems: List<TextPreview>,
    selectedText: TextUi?,
    onTextSelected: (Int) -> Unit,
    onClearSelection: () -> Unit,
    navigateToAddTextScreen: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    var isLocked by remember { mutableStateOf(false) }

    var isSwipeAttempted by remember { mutableStateOf(false) }

    val viewConfiguration = LocalViewConfiguration.current
    val customViewConfiguration = remember {
        object : ViewConfiguration by viewConfiguration {
            override val touchSlop: Float get() = viewConfiguration.touchSlop * 10f
        }
    }
    LaunchedEffect(isSwipeAttempted) {
        if (isSwipeAttempted) {
            delay(300)
            isSwipeAttempted = false
        }
    }
    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            if (isLocked || selectedText == null) {
                isSwipeAttempted = true
                return@launch
            }
            pagerState.animateScrollToPage(0)
            onClearSelection()
        }
    }
    CompositionLocalProvider(LocalViewConfiguration provides customViewConfiguration) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when (pagerState.currentPage) {
                                0 -> "My Texts"
                                1 -> selectedText?.title ?: "Text"
                                else -> "Settings"
                            }
                        )
                    },
                    actions = {
                        if (pagerState.currentPage == 1) {
                            IconButton(onClick = { isLocked = !isLocked }) {
                                AnimatedLockIcon(
                                    isLocked = isLocked,
                                    isSwipeAttempted = isSwipeAttempted
                                )
                            }
                        }

                        IconButton(onClick = {
                            coroutineScope.launch {
                                if (isLocked || selectedText == null) {
                                    isSwipeAttempted = true
                                    return@launch
                                }
                                pagerState.animateScrollToPage(2)
                            }
                        }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = navigateToAddTextScreen) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { paddingValues ->

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pointerInput(isLocked) {
                        if (isLocked) {
                            detectDragGestures { change, dragAmount ->
                                if (abs(dragAmount.x) > abs(dragAmount.y)) {
                                    isSwipeAttempted = true
                                    change.consume()
                                }
                            }
                        }
                    },
                userScrollEnabled = selectedText != null && !isLocked
            ) { page ->
                when (page) {
                    0 -> TextsListPage(items = textItems, onItemClick = { item ->
                        onTextSelected(item.id)
                        coroutineScope.launch { pagerState.animateScrollToPage(1) }
                    })

                    1 -> TextReaderPage(state = selectedText)
                    2 -> SettingsPage()
                }
            }
        }
    }
}


@Composable
fun AnimatedLockIcon(isLocked: Boolean, isSwipeAttempted: Boolean) {
    val haptic = LocalHapticFeedback.current

    val targetColor = when {
        isSwipeAttempted -> Color.Red
        isLocked -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(300),
        label = "LockColor"
    )

    val targetSize = if (isSwipeAttempted) 36.dp else 24.dp
    val animatedSize by animateDpAsState(
        targetValue = targetSize,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "LockSize"
    )

    LaunchedEffect(isSwipeAttempted) {
        if (isSwipeAttempted) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Icon(
        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
        contentDescription = "Lock Status",
        tint = animatedColor,
        modifier = Modifier.size(animatedSize)
    )
}

@Composable
fun SettingsPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Tune,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "Settings & Customization",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            "Тут ти зможеш додавати свої налаштування Regex та інше.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Composable
private fun TextsListPage(
    items: List<TextPreview>,
    onItemClick: (TextPreview) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            TextLazyItem(item = item, onClick = { onItemClick(item) })
        }
    }
}


@Composable
private fun TextLazyItem(
    item: TextPreview,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TextReaderPage(
    state: TextUi?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state == null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator()
                Text("Loading text...", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Text(
                text = state.content,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}