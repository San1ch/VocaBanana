package com.san1ch.vocabanana.feature.vocabulary.presentation.vocabularyscreenpages

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.san1ch.vocabanana.core.essentials.model.SortType
import com.san1ch.vocabanana.core.essentials.model.WordFilter
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.ui.compose.DeleteConfirmDialog
import com.san1ch.vocabanana.core.ui.compose.SearchBarField
import com.san1ch.vocabanana.core.ui.model.WordUi
import com.san1ch.vocabanana.core.ui.theme.AppColor
import com.san1ch.vocabanana.core.ui.theme.LocalDarkTheme
import com.san1ch.vocabanana.feature.vocabulary.presentation.R
import com.san1ch.vocabanana.feature.vocabulary.presentation.VocabMilestone
import com.san1ch.vocabanana.feature.vocabulary.presentation.VocabularyIntent
import com.san1ch.vocabanana.feature.vocabulary.presentation.VocabularyStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyListPage(
    words: List<WordUi>,
    stats: VocabularyStats,
    newWordsCount: Int,
    wordFilter: WordFilter,
    onIntent: (VocabularyIntent) -> Unit,
    onMenuClick: () -> Unit,
) {
    var wordToDelete by remember { mutableStateOf<WordUi?>(null) }
    // Pick a surface color for the "Connected" look
    val containerColor = MaterialTheme.colorScheme.surface

    var isSearchVisible by remember { mutableStateOf(false) }

    DeleteConfirmDialog(
        item = wordToDelete,
        onDismiss = { wordToDelete = null },
        onConfirm = {
            onIntent(VocabularyIntent.DeleteWord(it.id))
            wordToDelete = null
        },
    )

    Scaffold(
        topBar = {
            // Stack the AppBar and Stats together to make them look like one unit
            Column(modifier = Modifier.background(containerColor)) {
                TopAppBar(
                    title = { Text(stringResource(R.string.vocabulary)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = containerColor, // Same color as column
                    ),
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        NewWordsBadgeButton(
                            count = newWordsCount,
                            onClick = { onIntent(VocabularyIntent.NavigateToNewWords) },
                        )
                        IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                            Icon(
                                imageVector = if (isSearchVisible) Icons.Default.FilterList else Icons.Default.Search,
                                contentDescription = "Toggle Search",
                            )
                        }
                    },
                )

                SearchBarField(
                    query = wordFilter.searchQuery,
                    onQueryChange = { onIntent(VocabularyIntent.UpdateSearchQuery(it)) },
                    isVisible = isSearchVisible,
                )
                // The stats header now sits right under the title with NO gaps
                VocabularyStatsHeader(stats = stats, backgroundColor = containerColor)

                // Subtle line to separate the header from the scrolling list
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        },
    ) { padding ->
        // LazyColumn fills the rest of the screen
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding), // This padding now starts AFTER the combined header
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            items(words, key = { it.id }) { word ->
                WordListItem(
                    word = word,
                    onClick = { onIntent(VocabularyIntent.SelectWord(word.id)) },
                    onDelete = { wordToDelete = word },
                )
            }
        }
    }
}

@Composable
fun VocabularyDrawerContent(
    wordFilter: WordFilter,
    onIntent: (VocabularyIntent) -> Unit,
    onClose: () -> Unit,
) {
    val isDarkTheme = LocalDarkTheme.current
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(R.string.vocabulary_settings),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.filter_by_states),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(12.dp))

            val allStates = listOf(
                WordState.LEARNING to stringResource(R.string.learning_stats),
                WordState.NOT_KNOWN to stringResource(R.string.not_known_stats),
                WordState.KNOWN to stringResource(R.string.known_stats),
                WordState.IGNORED to stringResource(R.string.ignored_stats),
            )

            allStates.chunked(2).forEach { rowStates ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    rowStates.forEach { (state, label) ->
                        val isChecked = wordFilter.visibleStates.contains(state)
                        val stateColor = getStateColor(state, isDarkTheme)

                        FilterStateChipButton(
                            modifier = Modifier.weight(1f),
                            label = label,
                            stateColor = stateColor,
                            selected = isChecked,
                            onClick = {
                                val updatedStates = if (isChecked) {
                                    wordFilter.visibleStates - state
                                } else {
                                    wordFilter.visibleStates + state
                                }
                                onIntent(VocabularyIntent.UpdateVisibleStates(updatedStates))
                            },
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.sort_by),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
                IconButton(onClick = { onIntent(VocabularyIntent.ToggleSortOrder) }) {
                    Icon(
                        imageVector = if (wordFilter.isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = "Toggle Order",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            val sortOptions = listOf(
                SortType.ALPHABETIC to stringResource(R.string.alphabet),
                SortType.STATE to stringResource(R.string.status_state),
                SortType.DATE to stringResource(R.string.recently_added),
                SortType.COUNT to stringResource(R.string.count),
            )

            sortOptions.chunked(2).forEach { rowSorts ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    rowSorts.forEach { (sortType, label) ->
                        val isSelected = wordFilter.sortType == sortType

                        SortOptionChip(
                            modifier = Modifier.weight(1f),
                            label = label,
                            selected = isSelected,
                            onClick = {
                                onIntent(VocabularyIntent.ChangeSortType(sortType))
                                onClose()
                            },
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}


@Composable
fun SortOptionChip(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {

    val backgroundColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "sortBgColor",
    )


    val textColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        label = "sortTextColor",
    )

    val borderColor =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(
            alpha = 0.6f
        )

    Surface(
        modifier = modifier
            .height(40.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(1.5.dp, borderColor),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = textColor,
            )
        }
    }
}

@Composable
fun FilterStateChipButton(
    modifier: Modifier = Modifier,
    label: String,
    stateColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val isDarkTheme = LocalDarkTheme.current

    val backgroundColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) stateColor else Color.Transparent,
        label = "chipBackgroundColor",
    )

    val targetTextColor = when {
        selected && !isDarkTheme -> Color.White
        selected && isDarkTheme -> Color.Black
        else -> stateColor
    }

    val textColor by androidx.compose.animation.animateColorAsState(
        targetValue = targetTextColor,
        label = "chipTextColor",
    )

    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = if (!selected) BorderStroke(1.5.dp, stateColor) else null,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
            )
        }
    }
}

@Composable
fun getStateColor(state: WordState, isDarkTheme: Boolean): Color = when (state) {
    WordState.NEW -> MaterialTheme.colorScheme.secondary
    WordState.LEARNING -> if (isDarkTheme) {
        AppColor.Learn
    } else {
        Color(0xFFD39E00)
    }

    WordState.NOT_KNOWN -> AppColor.NotKnow
    WordState.KNOWN -> if (isDarkTheme) {
        AppColor.Known
    } else {
        Color(0xFF5BCC60)
    }

    WordState.IGNORED -> if (isDarkTheme) {
        AppColor.Ignore
    } else {
        Color(0xFF494949)
    }
}


@Composable
fun VocabularyStatsHeader(
    stats: VocabularyStats,
    // Pass the surface color here
    backgroundColor: Color = Color.Transparent,
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
            .animateContentSize(),
    ) {
        // Compact Progress Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(32.dp), // Fixed short height for the "closed" state
        ) {
            Text(
                text = "${stats.known}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            // Thinner Progress Bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .height(6.dp) // Decreased height from 8.dp to 6.dp
                    .background(
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        CircleShape,
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(nextMilestone.color, CircleShape),
                )
            }

            Text(
                text = "${nextMilestone.threshold}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
            )

            Icon(
                imageVector = if (isExpanded) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(16.dp),
                tint = MaterialTheme.colorScheme.outline,
            )
        }

        // Expanded content (The Donut Chart and specific stats)
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${nextMilestone.label}: ${nextMilestone.threshold - stats.known} more to go",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // Smaller Donut Chart
                    Box(modifier = Modifier.size(110.dp)) {
                        // Shorter chart
                        val donutChartData = PieChartData(
                            slices = listOf(
                                PieChartData.Slice(
                                    stringResource(R.string.known_stats),
                                    stats.known.toFloat(),
                                    AppColor.Known
                                ),
                                PieChartData.Slice(
                                    stringResource(R.string.learning_stats),
                                    stats.learning.toFloat(),
                                    AppColor.Learn,
                                ),
                                PieChartData.Slice(
                                    stringResource(R.string.not_known_stats),
                                    stats.notKnown.toFloat(),
                                    AppColor.NotKnow,
                                ),
                            ),
                            plotType = PlotType.Donut,
                        )
                        DonutPieChart(
                            modifier = Modifier.fillMaxSize(),
                            pieChartData = donutChartData,
                            pieChartConfig = PieChartConfig(
                                isAnimationEnable = true,
                                showSliceLabels = false,
                                backgroundColor = Color.Transparent,
                                strokeWidth = 20f,
                            ),
                        )
                    }

                    // Legend
                    Column(modifier = Modifier.padding(start = 24.dp)) {
                        StatRow(AppColor.Known, stringResource(R.string.known_stats), stats.known)
                        StatRow(
                            AppColor.Learn,
                            stringResource(R.string.learning_stats),
                            stats.learning
                        )
                        StatRow(
                            AppColor.NotKnow,
                            stringResource(R.string.not_known_stats),
                            stats.notKnown
                        )
                        StatRow(
                            AppColor.Ignore,
                            stringResource(R.string.ignored_stats),
                            stats.ignored
                        )
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
        modifier = Modifier.padding(vertical = 2.dp),
    ) {
        Box(
            Modifier
                .size(8.dp)
                .background(color, CircleShape),
        )
        Spacer(Modifier.width(8.dp))
        Text("$label: $count", style = MaterialTheme.typography.bodySmall)
    }
}


@Composable
fun WordListItem(
    word: WordUi,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val stateColor = when (word.state) {
        WordState.NOT_KNOWN -> AppColor.NotKnow
        WordState.LEARNING -> AppColor.Learn
        WordState.KNOWN -> AppColor.Known
        WordState.IGNORED -> AppColor.Ignore
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.surface
            } else {
                Color.White
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        ),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Status bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(stateColor),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
            ) {
                Text(
                    text = word.lemma,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = word.partOfSpeech.lowercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(end = 8.dp),
            ) {
                Text(
                    text = "×${word.count}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.padding(end = 4.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWordsBadgeButton(
    count: Int,
    onClick: () -> Unit,
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
                },
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = "New Words Pending",
                )
            }
        }
    }
}
