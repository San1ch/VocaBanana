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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.feature.text.presentation.data.TextPreview
import com.example.vocabanana.feature.text.presentation.data.TextUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun TextListScreen(
    viewModel: TextListScreenViewModel = hiltViewModel(),
    navigateToAddTextScreen: () -> Unit
) {
    val currentText by viewModel.currentText.collectAsState()
    val textPreviewsState by viewModel.textPreviews.collectAsState()
    StateObserver(
        state = textPreviewsState
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
            navigateToAddTextScreen = navigateToAddTextScreen,
            onProgressUpdate = { textId,position ->
                viewModel.updateProgress(textId,position)
            }
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
    navigateToAddTextScreen: () -> Unit,
    onProgressUpdate: (Int, Float) -> Unit
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
                                1 -> selectedText?.title ?: "Error"
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
                                    coroutineScope.launch {
                                        delay(300)
                                        isSwipeAttempted = false
                                    }
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

                    1 -> TextReaderPage(state = selectedText, onProgressUpdate = { textId, position ->
                        onProgressUpdate(textId, position)
                    })
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
            "TODO",
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
    state: TextUi?,
    onProgressUpdate: (Int, Float) -> Unit
) {
    // 1. Використовуємо один і той самий scrollState всюди
    val scrollState = rememberScrollState()

    // 2. Логіка збереження прогресу
    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        val id = state?.id
        if (id != null && scrollState.maxValue > 0) {
            val progress = scrollState.value.toFloat() / scrollState.maxValue
            onProgressUpdate(id, progress)
        }
    }

    // 3. Логіка ВІДНОВЛЕННЯ прогресу при відкритті
    // Чекаємо, поки maxValue стане більше 0 (текст завантажився в розмітку)
    LaunchedEffect(state?.id, scrollState.maxValue) {
        val savedProgress = state?.lastScrollPosition
        if (savedProgress != null && savedProgress > 0f && scrollState.maxValue > 0) {
            val targetScroll = (savedProgress * scrollState.maxValue).toInt()
            scrollState.scrollTo(targetScroll)
        }
    }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState) // ПЕРЕДАЄМО НАШ scrollState СЮДИ!
            ) {
                Text(
                    text = state.content,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}