package com.example.vocabanana.feature.text.presentation

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.core.presentation.toFormattedDate
import com.example.vocabanana.feature.text.presentation.data.TextPreview
import com.example.vocabanana.feature.text.presentation.data.TextUi
import com.example.vocabanana.ui.composable.AnimatedTitle
import com.example.vocabanana.ui.composable.CollectUiEvents
import com.example.vocabanana.ui.composable.DeleteConfirmDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun TextListScreen(
    viewModel: TextListScreenViewModel = hiltViewModel(),
    navigateTo: (AppDestination) -> Unit,
    navigateBack: () -> Unit,
) {
    val currentText by viewModel.currentText.collectAsState()
    val textPreviewsState by viewModel.textPreviews.collectAsState()
    var deletingText by remember { mutableStateOf<TextPreview?>(null) }

    DeleteConfirmDialog(
        item = deletingText,
        onDismiss = { deletingText = null },
        onConfirm = { text ->
            viewModel.deleteText(text.id)
            deletingText = null
        }
    )

    CollectUiEvents(events = viewModel.events, navigateBack = navigateBack, navigateTo = {})

    StateObserver(state = textPreviewsState) { textPreviews ->
        TextListContent(
            textItems = textPreviews,
            selectedText = currentText,
            onTextSelected = { id -> viewModel.selectText(id) },
            onClearSelection = viewModel::clearSelection,
            navigateToAddText = { navigateTo(AppDestination.TextCreate) },
            onProgressUpdate = { id, pos -> viewModel.updateProgress(id, pos) },
            onDelete = { preview -> deletingText = preview }
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
    navigateToAddText: () -> Unit,
    onProgressUpdate: (Int, Float) -> Unit,
    onDelete: (TextPreview) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    var isLocked by remember { mutableStateOf(false) }
    var isSwipeAttempted by remember { mutableStateOf(false) }

    SetupSwipeLockEffects(isSwipeAttempted) { isSwipeAttempted = false }

    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            if (isLocked || selectedText == null) {
                isSwipeAttempted = true; return@launch
            }
            pagerState.animateScrollToPage(0)
            onClearSelection()
        }
    }

    CustomTouchSlopProvider {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        val titleText = when (pagerState.currentPage) {
                            0 -> "My Texts"
                            1 -> selectedText?.title ?: "Loading..."
                            else -> "Settings"
                        }
                        AnimatedTitle(targetText = titleText)
                    },
                    actions = {
                        TopBarActions(
                            currentPage = pagerState.currentPage,
                            isLocked = isLocked,
                            isSwipeAttempted = isSwipeAttempted,
                            onLockClick = { isLocked = !isLocked },
                            onSettingsClick = {
                                coroutineScope.launch {
                                    if (isLocked || selectedText == null) isSwipeAttempted = true
                                    else pagerState.animateScrollToPage(2)
                                }
                            }
                        )
                    }
                )
            },
            floatingActionButton = {
                FabAnimated(visible = pagerState.currentPage == 0, onClick = navigateToAddText)
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pointerInput(isLocked) {
                        if (isLocked) detectDragGestures { change, dragAmount ->
                            if (abs(dragAmount.x) > abs(dragAmount.y)) {
                                isSwipeAttempted = true
                                coroutineScope.launch { delay(300); isSwipeAttempted = false }
                                change.consume()
                            }
                        }
                    },
                userScrollEnabled = selectedText != null && !isLocked
            ) { page ->
                when (page) {
                    0 -> TextListPage(
                        items = textItems,
                        onItemClick = { item ->
                            onTextSelected(item.id)
                            coroutineScope.launch { pagerState.animateScrollToPage(1) }
                        },
                        onDelete = onDelete
                    )

                    1 -> TextReaderPage(state = selectedText, onProgressUpdate = onProgressUpdate)
                    2 -> SettingsPage()
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

@Composable
private fun TextListPage(
    items: List<TextPreview>,
    onItemClick: (TextPreview) -> Unit,
    onDelete: (TextPreview) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
            Box(
                modifier = Modifier.animateItem(
                    placementSpec = spring(dampingRatio = 0.6f, stiffness = 800f)
                )
            ) {
                TextLazyItem(
                    item = item,
                    onClick = { onItemClick(item) },
                    onDelete = { onDelete(item) })
            }
        }
    }
}

@Composable
private fun TextLazyItem(item: TextPreview, onClick: () -> Unit, onDelete: (TextPreview) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.lastReadTime.toFormattedDate(),
                    style = MaterialTheme.typography.bodySmall
                )
                IconButton(onClick = { onDelete(item) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
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
fun AnimatedLockIcon(isLocked: Boolean, isSwipeAttempted: Boolean) {
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
        Text("Sentences: x / maxCount")
        Button(onClick = { }) {
            Text("Parse and clean")
        }

    }
}



@SuppressLint("FrequentlyChangingValue")
@Composable
private fun TextReaderPage(
    state: TextUi?,
    onProgressUpdate: (Int, Float) -> Unit
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        val id = state?.id
        if (id != null && scrollState.maxValue > 0) {
            val progress = scrollState.value.toFloat() / scrollState.maxValue
            onProgressUpdate(id, progress)
        }
    }
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
                    .verticalScroll(scrollState)
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