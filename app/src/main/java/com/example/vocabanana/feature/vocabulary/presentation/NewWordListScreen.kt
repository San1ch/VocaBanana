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
            .padding(vertical = 4.dp)
            .animateContentSize(),
        onClick = { expanded = !expanded }
    ) {
        // Outer Row has NO horizontal padding so the selector can touch the edge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp), // Fixed height for a uniform look
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Word Info with specific padding
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp), // Only pad the left side
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = word.lemma,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box(modifier = Modifier.height(20.dp)) {
                    if (word.partOfSpeech.isNotBlank()) {
                        Text(
                            text = word.partOfSpeech,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Text(text = "In Texts: ${word.countInTheTexts}")
            // Right: The Selector - No right/top/bottom padding
            // It fills the height of the Row (80.dp)
            Spacer(modifier = Modifier.width(16.dp))
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
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    "Forms: ${word.forms.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (word.definition.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(word.definition, style = MaterialTheme.typography.bodySmall)
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
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}