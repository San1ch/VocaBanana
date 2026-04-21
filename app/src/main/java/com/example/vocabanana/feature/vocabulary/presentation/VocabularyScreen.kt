package com.example.vocabanana.feature.vocabulary.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.vocabanana.R
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.core.word.domain.model.PartOfSpeech
import com.example.vocabanana.core.word.domain.model.WordState
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.feature.vocabulary.data.VocabMilestone
import com.example.vocabanana.feature.vocabulary.data.VocabularyStats
import com.example.vocabanana.ui.composable.CollectUiEvents
import com.example.vocabanana.ui.composable.DeleteConfirmDialog
import com.example.vocabanana.ui.theme.AppColor
import kotlinx.coroutines.launch

enum class SortType {
    ALPHABETIC,
    STATE,
    COUNT,
    DATE
}

sealed interface VocabularyIntent {
    data class SelectWord(val id: Int) : VocabularyIntent
    data class DeleteWord(val id: Int) : VocabularyIntent
    data class ChangeSortType(val sortType: SortType) : VocabularyIntent
    data object ToggleSortOrder : VocabularyIntent
    data object NavigateToNewWords : VocabularyIntent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    viewModel: VocabularyScreenViewModel = hiltViewModel(),
    navigateTo: (AppDestination) -> Unit,
    navigateBack: () -> Unit
) {

    CollectUiEvents(
        viewModel.events,
        navigateBack = navigateBack,
        navigateTo = navigateTo,
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val words by viewModel.words.collectAsState()
    val selectedId by viewModel.selectedWordId.collectAsState()
    val newWordsCount by viewModel.newWordsCount.collectAsState()

    val sortType by viewModel.sortType.collectAsState()
    val isAscending by viewModel.isAscending.collectAsState()

    val stats by viewModel.stats.collectAsState()

    val pagerState = rememberPagerState(pageCount = { 2 })

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            VocabularyDrawerContent(
                currentSort = sortType,
                isAscending = isAscending,
                onIntent = { viewModel.onIntent(it) },
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        StateObserver(words) { words ->
            val selectedWord = words.find { it.id == selectedId }

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = selectedWord != null,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> VocabularyListContent(
                        words = words,
                        stats = stats,
                        newWordsCount = newWordsCount,
                        onIntent = { intent ->
                            viewModel.onIntent(intent)
                            if (intent is VocabularyIntent.SelectWord) {
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            }
                        },
                        onMenuClick = { scope.launch { drawerState.open() } },
                    )

                    1 -> WordDetailContent(
                        word = selectedWord,
                        onBack = {
                            scope.launch { pagerState.animateScrollToPage(0) }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyListContent(
    words: List<WordUi>,
    stats: VocabularyStats,
    newWordsCount: Int,
    onIntent: (VocabularyIntent) -> Unit,
    onMenuClick: () -> Unit
) {
    var wordToDelete by remember { mutableStateOf<WordUi?>(null) }
    // Pick a surface color for the "Connected" look
    val containerColor = MaterialTheme.colorScheme.surface

    DeleteConfirmDialog(
        item = wordToDelete,
        onDismiss = { wordToDelete = null },
        onConfirm = {
            onIntent(VocabularyIntent.DeleteWord(it.id))
            wordToDelete = null
        }
    )

    Scaffold(
        topBar = {
            // Stack the AppBar and Stats together to make them look like one unit
            Column(modifier = Modifier.background(containerColor)) {
                TopAppBar(
                    title = { Text(stringResource(R.string.vocabulary)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = containerColor // Same color as column
                    ),
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        NewWordsBadgeButton(
                            count = newWordsCount,
                            onClick = { onIntent(VocabularyIntent.NavigateToNewWords) }
                        )
                        /*IconButton(onClick = {  }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }*/
                    }
                )

                // The stats header now sits right under the title with NO gaps
                VocabularyStatsHeader(stats = stats, backgroundColor = containerColor)

                // Subtle line to separate the header from the scrolling list
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    ) { padding ->
        // LazyColumn fills the rest of the screen
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding), // This padding now starts AFTER the combined header
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(words, key = { it.id }) { word ->
                WordListItem(
                    word = word,
                    onClick = { onIntent(VocabularyIntent.SelectWord(word.id)) },
                    onDelete = { wordToDelete = word }
                )
            }
        }
    }
}

@Composable
fun VocabularyDrawerContent(
    currentSort: SortType,
    isAscending: Boolean,
    onIntent: (VocabularyIntent) -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight()) {
            Text("Vocabulary Settings", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(24.dp))

            // Tags Section
            Text("Tags", style = MaterialTheme.typography.labelLarge)
            Text("No tags yet", modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)

            // The Separator "Stick"
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Sorting Header with Order Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sort By", style = MaterialTheme.typography.labelLarge)
                IconButton(onClick = { onIntent(VocabularyIntent.ToggleSortOrder) }) {
                    Icon(
                        imageVector = if (isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = "Toggle Order",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            SortOption(
                label = "Alphabet",
                selected = currentSort == SortType.ALPHABETIC,
                onClick = { onIntent(VocabularyIntent.ChangeSortType(SortType.ALPHABETIC)); onClose() }
            )
            SortOption(
                label = "Status/State",
                selected = currentSort == SortType.STATE,
                onClick = { onIntent(VocabularyIntent.ChangeSortType(SortType.STATE)); onClose() }
            )
            SortOption(
                label = "Recently Added",
                selected = currentSort == SortType.DATE,
                onClick = { onIntent(VocabularyIntent.ChangeSortType(SortType.DATE)); onClose() }
            )
            SortOption(
                label = "Count in Texts",
                selected = currentSort == SortType.COUNT,
                onClick = { onIntent(VocabularyIntent.ChangeSortType(SortType.COUNT)); onClose() }
            )
        }
    }
}

@Composable
fun VocabularyStatsHeader(
    stats: VocabularyStats,
    backgroundColor: Color = Color.Transparent // Pass the surface color here
) {
    var isExpanded by remember { mutableStateOf(false) }
    val nextMilestone = VocabMilestone.getNext(stats.totalLemmas)
    val progress = stats.known.toFloat() / nextMilestone.threshold.toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { isExpanded = !isExpanded }
            .padding(horizontal = 16.dp, vertical = 4.dp) // Very small vertical padding
            .animateContentSize()
    ) {
        // Compact Progress Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(32.dp) // Fixed short height for the "closed" state
        ) {
            Text(
                text = "${stats.known}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Thinner Progress Bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .height(6.dp) // Decreased height from 8.dp to 6.dp
                    .background(
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(nextMilestone.color, CircleShape)
                )
            }

            Text(
                text = "${nextMilestone.threshold}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Icon(
                imageVector = if (isExpanded) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }

        // Expanded content (The Donut Chart and specific stats)
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${nextMilestone.label}: ${nextMilestone.threshold - stats.known} more to go",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Smaller Donut Chart
                    Box(modifier = Modifier.size(110.dp)) { // Shorter chart
                        val donutChartData = PieChartData(
                            slices = listOf(
                                PieChartData.Slice("Known", stats.known.toFloat(), AppColor.Known),
                                PieChartData.Slice(
                                    "Learning",
                                    stats.learning.toFloat(),
                                    AppColor.Learn
                                ),
                                PieChartData.Slice(
                                    "Not Known",
                                    stats.notKnown.toFloat(),
                                    AppColor.NotKnow
                                ),
                            ),
                            plotType = PlotType.Donut
                        )
                        DonutPieChart(
                            modifier = Modifier.fillMaxSize(),
                            pieChartData = donutChartData,
                            pieChartConfig = PieChartConfig(
                                isAnimationEnable = true,
                                showSliceLabels = false,
                                backgroundColor = Color.Transparent,
                                strokeWidth = 20f // Thinner donut ring
                            )
                        )
                    }

                    // Legend
                    Column(modifier = Modifier.padding(start = 24.dp)) {
                        StatRow(AppColor.Known, "Known", stats.known)
                        StatRow(AppColor.Learn, "Learning", stats.learning)
                        StatRow(AppColor.NotKnow, "Not Known", stats.notKnown)
                        StatRow(AppColor.Ignore, "Ignored", stats.ignored)
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(color: Color, label: String, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.width(8.dp))
        Text("$label: $count", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SortOption(label: String, selected: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
fun WordListItem(
    word: WordUi,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val stateColor = when (word.state) {
        WordState.NOT_KNOWN -> AppColor.NotKnow
        WordState.LEARNING -> AppColor.Learn
        WordState.KNOWN -> AppColor.Known
        WordState.IGNORED -> AppColor.Ignore
        else -> Color.Gray
    }

    // Modern card: Solid color, subtle border, no weird "frame"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            // If it's light theme, we use pure white. If dark, a slightly lighter gray than background.
            containerColor = if (isSystemInDarkTheme())
                MaterialTheme.colorScheme.surface
            else Color.White
        ),
        // Use either elevation or border. For a clean look, let's use a soft border:
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status bar
            Box(modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(stateColor))

            Column(modifier = Modifier
                .weight(1f)
                .padding(16.dp)) {
                Text(
                    text = word.lemma,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = word.partOfSpeech.lowercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.padding(end = 4.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailContent(
    word: WordUi?,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(word?.lemma ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (word != null) {
                Text("Lemma: ${word.lemma}", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "POS: ${word.partOfSpeech}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(16.dp))

                Text("Forms:", style = MaterialTheme.typography.titleMedium)
                word.forms.forEach { form ->
                    Text("• $form", style = MaterialTheme.typography.bodyMedium)
                }


            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Select a word to see details")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWordsBadgeButton(
    count: Int,
    onClick: () -> Unit
) {
    if (count > 0) {
        val textCount = when (count) {
            in 1..99 -> count.toString()
            else -> "99+"
        }
        IconButton(onClick = onClick) {
            BadgedBox(
                modifier = Modifier.padding(top = 4.dp, end = 13.dp),
                badge = {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ) {
                        Text(textCount)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = "New Words Pending"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordEditContent(
    word: WordUi?,
    onBack: () -> Unit,
    onSave: (WordUi) -> Unit
) {
    // Local state for editing
    var definition by remember(word) { mutableStateOf(word?.definition ?: "") }
    var selectedPos by remember(word) { mutableStateOf(word?.partOfSpeech ?: "UNKNOWN") }
    var expanded by remember { mutableStateOf(false) }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit ${word?.lemma}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Cancel")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        word?.let {
                            onSave(
                                it.copy(
                                    definition = definition,
                                    partOfSpeech = selectedPos
                                )
                            )
                        }
                    }) {
                        Text("SAVE")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Lemma: ${word?.lemma}", style = MaterialTheme.typography.labelLarge)
            Text("You cannot change the lemma.", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(16.dp))

            Text("Part of Speech", style = MaterialTheme.typography.titleMedium)
            Box {
                OutlinedTextField(
                    value = selectedPos,
                    onValueChange = {},
                    readOnly = true, // User must pick from menu
                    trailingIcon = { Icon(Icons.Default.FilterList, null) },
                    modifier = Modifier
                        .clickable { expanded = true }
                        .fillMaxWidth()
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    PartOfSpeech.entries.forEach { pos ->
                        DropdownMenuItem(
                            text = { Text(pos.name) },
                            onClick = {
                                selectedPos = pos.name
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Definition Field
            OutlinedTextField(
                value = definition,
                onValueChange = { definition = it },
                label = { Text("Definition / Meaning") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }
    }
}
/*

@Preview
@Composable
fun WordListItemPreview() {
    WordListItem(
        word = WordUi(
            id = 0,
            lemma = "Test 2",
            whenAdded = 231239213L,
            state = WordState.LEARNING,
            definition = "",
            partOfSpeech = PartOfSpeech.ADJECTIVE.shortName,
            forms = listOf("Test Form 2")
        ),
        onClick = { }
    ) { }
}*/
