@file:Suppress("DEPRECATION")

package com.example.vocabanana.feature.text.presentation

import android.content.Intent
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.core.presentation.toFormattedDate
import com.example.vocabanana.feature.text.presentation.data.GenerateWordsFromTextUiState
import com.example.vocabanana.feature.text.presentation.data.ParagraphUi
import com.example.vocabanana.feature.text.presentation.data.TextPreview
import com.example.vocabanana.feature.text.presentation.data.TextToken
import com.example.vocabanana.feature.text.presentation.data.TextUi
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.feature.text.presentation.data.tokenize
import com.example.vocabanana.ui.composable.AnimatedTitle
import com.example.vocabanana.ui.composable.CollectUiEvents
import com.example.vocabanana.ui.composable.DeleteConfirmDialog
import com.example.vocabanana.ui.composable.DpSizes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

sealed class WordInfoState {
    object Hidden : WordInfoState()
    object Loading : WordInfoState()
    data class Found(val word: WordUi) : WordInfoState()
    data class NotFound(val word: String) : WordInfoState()
}

@Composable
fun TextListScreen(
    viewModel: TextListScreenViewModel = hiltViewModel(),
    navigateTo: (AppDestination) -> Unit,
    navigateBack: () -> Unit,
) {
    val currentText by viewModel.currentText.collectAsState()
    val textPreviewsState by viewModel.textPreviews.collectAsState()
    var deletingText by remember { mutableStateOf<TextPreview?>(null) }

    val generatingState by viewModel.generatingState.collectAsState()

    val wordInfoState by viewModel.wordInfoState.collectAsState()

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
            onDelete = { preview -> deletingText = preview },
            onGenerateWords = { viewModel.generateWords() },
            generatingState = generatingState,
            onWordClick = { word -> viewModel.selectWordInPage(word) },
            wordInfoState = wordInfoState,
            closeWordInfo = viewModel::closeWordInfo
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
    onDelete: (TextPreview) -> Unit,
    onGenerateWords: () -> Unit,
    generatingState: GenerateWordsFromTextUiState?,
    onWordClick: (String) -> Unit,
    wordInfoState: WordInfoState,
    closeWordInfo: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    var isLocked by remember { mutableStateOf(false) }
    var isSwipeAttempted by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
        // BOX is the root to allow layering the Popup over the Scaffold
        Box(modifier = Modifier.fillMaxSize()) {
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
                                        if (isLocked || selectedText == null) isSwipeAttempted =
                                            true
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

                        1 -> TextReaderPage(
                            text = selectedText,
                            onProgressUpdate = onProgressUpdate,
                            onWordClick = onWordClick
                        )

                        2 -> TextListSettingsPage(
                            onGenerateWords = onGenerateWords,
                            generatingState = generatingState
                        )
                    }
                }
            }

            // WINDOW IS HERE: Outside Scaffold = On top of TopBar
            WordInfoPopup(
                state = wordInfoState,
                onDismiss = closeWordInfo, // Fixed: Passing the actual close logic
                onOxfordClick = { lemma ->
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.oxfordlearnersdictionaries.com/definition/english/$lemma".toUri()
                    )
                    context.startActivity(intent)
                }
            )
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
fun TextListSettingsPage(
    onGenerateWords: () -> Unit,
    generatingState: GenerateWordsFromTextUiState?
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Vocabulary Generator",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )


                Button(
                    onClick = { onGenerateWords() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    enabled = generatingState !is GenerateWordsFromTextUiState.Loading
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Generate Words", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        AnimatedVisibility(
            visible = generatingState != null,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            GeneratingWordStatus(generatingState)
        }
    }
}

@Composable
private fun GeneratingWordStatus(state: GenerateWordsFromTextUiState?) {
    if (state == null) return

    // This is the ONLY place context is allowed!
    val context = LocalContext.current

    val message = when (state) {
        is GenerateWordsFromTextUiState.Loading -> state.message.asString(context)
        is GenerateWordsFromTextUiState.Success -> state.message.asString(context)
        is GenerateWordsFromTextUiState.Error -> state.message.asString(context)
        is GenerateWordsFromTextUiState.AllExist -> state.message.asString(context)
        is GenerateWordsFromTextUiState.PartialSuccess -> state.message.asString(context)
    }

    val (color, icon) = when (state) {
        is GenerateWordsFromTextUiState.Success -> Color(0xFF4CAF50) to Icons.Default.CheckCircle
        is GenerateWordsFromTextUiState.PartialSuccess -> Color(0xFFFF9800) to Icons.Default.Warning
        is GenerateWordsFromTextUiState.Error -> MaterialTheme.colorScheme.error to Icons.Default.Warning
        is GenerateWordsFromTextUiState.Loading -> MaterialTheme.colorScheme.primary to Icons.Default.Refresh
        is GenerateWordsFromTextUiState.AllExist -> MaterialTheme.colorScheme.secondary to Icons.Default.Info
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        // We add an animation for the loading state later!
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            textAlign = TextAlign.Center
        )

        if (state is GenerateWordsFromTextUiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        }
    }


    /* TODO
    *   1. Fix main menu buttons in the light theme (something like in the vocab screen items
    *   2. Add text visual settings size, font and other
    *   3. Fix problem with top status bar in the light theme
    *   4. Add new word count in the new word screen
    *   5. Change style for text list screen and general light theme and its the TopAppBar problem
    * */
}

@Composable
fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TextReaderPage(
    text: TextUi?,
    onProgressUpdate: (Int, Float) -> Unit,
    onWordClick: (String) -> Unit
) {
    val listState = rememberLazyListState()
    var isScrollRestored by remember(text?.id) { mutableStateOf(false) }

    LaunchedEffect(text?.id, text?.paragraphs?.size) {
        val savedProgress = text?.lastScrollPosition
        val totalItems = text?.paragraphs?.size ?: 0

        if (totalItems > 0 && !isScrollRestored) {
            if (savedProgress != null && savedProgress > 0f) {
                val targetIndex = (savedProgress * totalItems).toInt()
                listState.scrollToItem(targetIndex)
            }
            isScrollRestored = true
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        val id = text?.id
        val totalItems = listState.layoutInfo.totalItemsCount
        if (id != null && totalItems > 0 && isScrollRestored) {
            val progress = listState.firstVisibleItemIndex.toFloat() / totalItems
            onProgressUpdate(id, progress)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (text == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(DpSizes.small)
                    .graphicsLayer { alpha = if (isScrollRestored) 1f else 0f }
            ) {
                items(text.paragraphs) { paragraph ->
                    ParagraphViewItem(
                        paragraph = paragraph,
                        onWordClick = { word -> onWordClick(word) }
                    )
                }
            }

            if (!isScrollRestored) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ParagraphViewItem(paragraph: ParagraphUi, onWordClick: (String) -> Unit) {
    val textColor = MaterialTheme.colorScheme.onSurface

    val annotatedString = remember(paragraph.rawText) {
        buildAnnotatedString {
            val tokens = paragraph.rawText.tokenize()
            tokens.forEach { token ->
                if (token is TextToken.Word) {
                    pushStringAnnotation(tag = "WORD", annotation = token.text)
                    // Change 'color = color' to 'textColor' or keep it subtle
                    withStyle(style = SpanStyle(color = textColor, fontWeight = FontWeight.Normal)) {
                        append(token.text)
                    }
                    pop()
                } else if (token is TextToken.Symbol) {
                    append(token.text)
                }
            }
        }
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "WORD", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onWordClick(annotation.item)
                }
        }
    )
}

@Composable
fun WordInfoPopup(
    state: WordInfoState,
    onDismiss: () -> Unit,
    onOxfordClick: (String) -> Unit
) {
    AnimatedVisibility(
        visible = state !is WordInfoState.Hidden,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        // Full screen transparent box to catch clicks outside
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = true, onClick = onDismiss)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable(enabled = false) {},
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    when (state) {
                        is WordInfoState.Loading -> CircularProgressIndicator(Modifier.size(24.dp))

                        is WordInfoState.NotFound -> {
                            Text("Word not in vocabulary", fontWeight = FontWeight.Bold)
                            OxfordButton(state.word, onOxfordClick)
                        }

                        is WordInfoState.Found -> {
                            // Lemma and POS
                            Text(
                                text = "${state.word.lemma} (${state.word.partOfSpeech})",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Forms joined by comma
                            if (state.word.forms.isNotEmpty()) {
                                Text(
                                    text = state.word.forms.joinToString(", "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            // Definition
                            Text(
                                text = state.word.definition.ifEmpty { "No definition found for this word." },
                                style = MaterialTheme.typography.bodyMedium
                            )

                            OxfordButton(state.word.lemma, onOxfordClick)
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun OxfordButton(word: String, onClick: (String) -> Unit) {
    TextButton(
        onClick = { onClick(word) },
        contentPadding = PaddingValues(0.dp)
    ) {
        Text("Oxford Dictionary →", style = MaterialTheme.typography.labelSmall)
    }
}

