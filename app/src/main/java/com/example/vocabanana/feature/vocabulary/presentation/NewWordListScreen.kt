package com.example.vocabanana.feature.vocabulary.presentation


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.core.word.domain.model.WordState
import com.example.vocabanana.ui.composable.CollectUiEvents


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
            TopAppBar(
                title = { Text("New Words") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Legend / Hint section
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem(Color.Red, "Don't know")
                    LegendItem(Color.Yellow, "Want to learn")
                    LegendItem(Color.Green, "Known")
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
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
            .animateContentSize(), // Smoothly animates the expansion
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Left: Word Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = word.lemma, style = MaterialTheme.typography.titleLarge)
                    Text(text = word.partOfSpeech, style = MaterialTheme.typography.bodySmall)
                }

                // Right: The Three Choice Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StateCircle(color = Color.Red, isSelected = word.state == WordState.NOT_KNOWN) {
                        onStateSelected(WordState.NOT_KNOWN)
                    }
                    StateCircle(
                        color = Color.Yellow,
                        isSelected = word.state == WordState.LEARNING
                    ) {
                        onStateSelected(WordState.LEARNING)
                    }
                    StateCircle(color = Color.Green, isSelected = word.state == WordState.KNOWN) {
                        onStateSelected(WordState.KNOWN)
                    }
                }
            }

            // Expanded Content
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(text = "Forms:", style = MaterialTheme.typography.labelLarge)
                    Text(text = word.forms.joinToString(", "))

                    if (word.definition.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(text = "Definition:", style = MaterialTheme.typography.labelLarge)
                        Text(text = word.definition)
                    }
                }
            }
        }
    }
}

@Composable
fun StateCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) color else Color.Transparent,
        label = "ring"
    )

    Box(
        modifier = Modifier
            .size(32.dp)
            .border(2.dp, borderColor, CircleShape) // The "Selection Ring"
            .padding(4.dp) // Gap between ring and circle
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}