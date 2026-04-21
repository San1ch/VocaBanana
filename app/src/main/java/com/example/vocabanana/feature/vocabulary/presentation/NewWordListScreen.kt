package com.example.vocabanana.feature.vocabulary.presentation


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.core.word.domain.model.WordState
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.ui.composable.CollectUiEvents
import com.example.vocabanana.ui.theme.AppColor


@Composable
fun NewWordListScreen(
    viewModel: NewWordListScreenViewModel = hiltViewModel(),
    navigateTo: (AppDestination) -> Unit,
    navigateBack: () -> Unit,
) {
    CollectUiEvents(
        events = viewModel.events,
        navigateBack = navigateBack,
        navigateTo = { navigateTo(it) })

    val wordsState by viewModel.newWords.collectAsState()

    StateObserver(wordsState) { words ->
        NewWordListContent(
            words = words,
            onBack = navigateBack,
            onStateChange = { id, state -> viewModel.updateWordState(id, state) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWordListContent(
    words: List<WordUi>,
    onBack: () -> Unit,
    onStateChange: (Int, WordState) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("New Words") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Legend with the new "Ignore" state
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem(AppColor.NotKnow, "Not known")
                    LegendItem(AppColor.Learn, "Learn")
                    LegendItem(AppColor.Known, "Known")
                    LegendItem(AppColor.Ignore, "Ignore")
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(words, key = { it.id }) { word ->
                    NewWordItem(
                        word = word,
                        onStateSelected = { newState -> onStateChange(word.id, newState) }
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier
            .size(12.dp)
            .background(color, CircleShape))
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}
@Composable
fun NewWordItem(
    word: WordUi,
    onStateSelected: (WordState) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .animateContentSize(),
        onClick = { expanded = !expanded }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Main Info Section
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = word.lemma,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (word.partOfSpeech.isNotBlank()) {
                            Text(
                                text = word.partOfSpeech,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(" • ", color = MaterialTheme.colorScheme.outline)
                        }
                        Text(
                            text = "Found ${word.countInTheTexts} times",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // The State Selector (Windows Style)
                WindowsStyleSelector(
                    currentState = word.state,
                    onStateSelected = onStateSelected,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(80.dp)
                )
            }

            // Expanded Details Section
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Forms Section
                    Column {
                        Text(
                            text = "FORMS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = word.forms.joinToString(", ").ifEmpty { "No other forms" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Definition Section
                    if (word.definition.isNotBlank()) {
                        Column {
                            Text(
                                text = "DEFINITION",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = word.definition,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WindowsStyleSelector(
    currentState: WordState,
    onStateSelected: (WordState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(Color.Black.copy(alpha = 0.05f)),
        verticalArrangement = Arrangement.spacedBy(0.dp) // Removed spacing for "Full" look
    ) {
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
            LogoQuadrant(AppColor.NotKnow, currentState == WordState.NOT_KNOWN) {
                onStateSelected(
                    WordState.NOT_KNOWN
                )
            }
            LogoQuadrant(AppColor.Learn, currentState == WordState.LEARNING) {
                onStateSelected(
                    WordState.LEARNING
                )
            }
        }
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
            LogoQuadrant(AppColor.Known, currentState == WordState.KNOWN) {
                onStateSelected(
                    WordState.KNOWN
                )
            }
            LogoQuadrant(AppColor.Ignore, currentState == WordState.IGNORED) {
                onStateSelected(
                    WordState.IGNORED
                )
            }
        }
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