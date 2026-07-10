package com.san1ch.vocabanana.feature.vocabulary.presentation


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.ui.state.StateObserver
import com.san1ch.vocabanana.core.ui.model.WordUi
import com.san1ch.vocabanana.core.ui.compose.CollectUiEvents
import com.san1ch.vocabanana.core.ui.compose.SearchBarField
import com.san1ch.vocabanana.core.ui.theme.AppColor

@Composable
fun NewWordListScreen(
    viewModel: NewWordListScreenViewModel = hiltViewModel()
) {
    CollectUiEvents(
        events = viewModel.events
    )

    val state by viewModel.uiState.collectAsState()

    StateObserver(state.words) { words ->
        NewWordListContent(
            state = state,
            words = words,
            onIntent = viewModel::onIntent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWordListContent(
    state: NewWordListState,
    words: List<WordUi>,
    onIntent: (NewWordListIntent) -> Unit
) {
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }

    // FIX: Local state keeps the keyboard snappy despite the ViewModel debounce
    var localSearchQuery by rememberSaveable { mutableStateOf(state.filter.searchQuery) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                TopAppBar(
                    title = { Text(stringResource(R.string.new_words_title)) },
                    navigationIcon = {
                        IconButton(onClick = { onIntent(NewWordListIntent.NavigateBack) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                            Icon(
                                imageVector = if (isSearchVisible) Icons.Default.FilterList else Icons.Default.Search,
                                contentDescription = "Toggle Search"
                            )
                        }
                        Text("${words.size}", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.width(16.dp))
                    }
                )

                SearchBarField(
                    query = localSearchQuery,
                    onQueryChange = { newText ->
                        localSearchQuery = newText
                        onIntent(NewWordListIntent.UpdateSearchQuery(newText))
                    },
                    isVisible = isSearchVisible
                )

                LegendHeader()
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(12.dp)
        ) {
            items(words, key = { it.id }) { word ->
                NewWordItem(
                    word = word,
                    onStateSelected = { newState ->
                        onIntent(NewWordListIntent.ChangeWordState(word.id, newState))
                    }
                )
            }
        }
    }
}

@Composable
fun LegendHeader() {
    Surface(
        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(AppColor.NotKnow, stringResource(R.string.word_state_not_known))
            LegendItem(AppColor.Learn, stringResource(R.string.word_state_learn))
            LegendItem(AppColor.Known, stringResource(R.string.word_state_known))
            LegendItem(AppColor.Ignore, stringResource(R.string.word_state_ignore))
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun NewWordItem(word: WordUi, onStateSelected: (WordState) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .animateContentSize(),
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme())
                MaterialTheme.colorScheme.surface
            else Color.White
        ),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = word.lemma, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = word.partOfSpeech,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 12.0.dp)
                ) {
                    Text(
                        text = "×${word.count}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                WindowsStyleSelector(
                    currentState = word.state,
                    onStateSelected = onStateSelected,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(80.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(16.dp)) {
                    HorizontalDivider(Modifier.padding(bottom = 12.dp))
                    DetailSection(
                        stringResource(R.string.forms_uppercase),
                        word.forms.joinToString(", ")
                    )
                    if (word.definition.isNotBlank()) {
                        DetailSection(
                            stringResource(R.string.definition_uppercase),
                            word.definition
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSection(label: String, content: String) {
    Column(Modifier.padding(bottom = 8.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(content, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun WindowsStyleSelector(
    currentState: WordState,
    onStateSelected: (WordState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.background(Color.Black.copy(alpha = 0.05f))) {
        Row(Modifier.weight(1f)) {
            Quadrant(AppColor.NotKnow, currentState == WordState.NOT_KNOWN) {
                onStateSelected(
                    WordState.NOT_KNOWN
                )
            }
            Quadrant(
                AppColor.Learn,
                currentState == WordState.LEARNING
            ) { onStateSelected(WordState.LEARNING) }
        }
        Row(Modifier.weight(1f)) {
            Quadrant(
                AppColor.Known,
                currentState == WordState.KNOWN
            ) { onStateSelected(WordState.KNOWN) }
            Quadrant(
                AppColor.Ignore,
                currentState == WordState.IGNORED
            ) { onStateSelected(WordState.IGNORED) }
        }
    }
}

@Composable
private fun RowScope.Quadrant(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(if (isSelected) color else color.copy(alpha = 0.2f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) Icon(
            Icons.Default.Check,
            null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun RowScope.LogoQuadrant(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(if (isSelected) color else color.copy(alpha = 0.7f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}