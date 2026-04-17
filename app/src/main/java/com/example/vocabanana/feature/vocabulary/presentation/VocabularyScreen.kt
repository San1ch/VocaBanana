package com.example.vocabanana.feature.vocabulary.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.R
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.core.presentation.UiEvent
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.ui.composable.CollectUiEvents
import com.example.vocabanana.ui.composable.DeleteConfirmDialog
import kotlinx.coroutines.launch

@Composable
fun VocabularyScreen(
    viewModel: VocabularyScreenViewModel = hiltViewModel(),
    navigateTo: (AppDestination) -> Unit,
    navigateBack: () -> Unit
) {
    CollectUiEvents(
        events = viewModel.events,
        navigateBack = navigateBack,
        navigateTo = navigateTo
    )

    val wordsState by viewModel.words.collectAsState()
    val selectedId by viewModel.selectedWordId.collectAsState()
    val newWordsCount by viewModel.newWordsCount.collectAsState()

    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    StateObserver(wordsState) { words ->
        val selectedWord = words.find { it.id == selectedId }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = selectedWord != null,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> VocabularyListContent(
                    words = words,
                    newWordsCount = newWordsCount,
                    onWordClick = { id ->
                        viewModel.selectWord(id)
                        scope.launch { pagerState.animateScrollToPage(1) }
                    },
                    onDeleteWord = { viewModel.deleteWord(it.id) },
                    onNewWordsClick = {
                        viewModel.sendEvent(UiEvent.NavigateTo(AppDestination.NewWordList))
                    }
                )

                1 -> WordDetailContent(
                    word = selectedWord,
                    onBack = {
                        scope.launch { pagerState.animateScrollToPage(0) }
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyListContent(
    words: List<WordUi>,
    newWordsCount: Int,
    onWordClick: (Int) -> Unit,
    onDeleteWord: (WordUi) -> Unit,
    onNewWordsClick: () -> Unit
) {
    var wordToDelete by remember { mutableStateOf<WordUi?>(null) }

    DeleteConfirmDialog(
        item = wordToDelete,
        onDismiss = { wordToDelete = null },
        onConfirm = { onDeleteWord(it); wordToDelete = null }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.vocabulary)) },
                actions = {
                    // Show badge button only if count > 0
                    NewWordsBadgeButton(
                        count = newWordsCount,
                        onClick = onNewWordsClick
                    )

                    VocabularyFilterButton() // Your existing filter

                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(words) { word ->
                ListItem(
                    modifier = Modifier.clickable { onWordClick(word.id) },
                    headlineContent = { Text(word.lemma) },
                    supportingContent = { Text(word.partOfSpeech ?: "") },
                    trailingContent = {
                        IconButton(onClick = { wordToDelete = word }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailContent(
    word: WordUi?,
    onBack: () -> Unit
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

@Composable
private fun VocabularyFilterButton() {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.FilterList, contentDescription = "Filter")
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        // TODO change from that to normal architecture and make
        DropdownMenuItem(
            onClick = {
                expanded = false
                //TODO
            },
            text = { Text("By alphabet") }
        )
        DropdownMenuItem(
            onClick = {
                expanded = false
                //TODO
            },
            text = { Text("By state") }
        )
        DropdownMenuItem(
            onClick = {
                expanded = false
                //TODO
            },
            text = { Text("By word") }
        )
        DropdownMenuItem(
            onClick = {
                expanded = false
                //TODO
            },
            text = { Text("By date") }
        )
    }
}

@Composable
fun AddWordInline(
    onAddClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
    showDialog: Boolean
) {
    var text by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
                text = ""
            },
            title = { Text(stringResource(R.string.add_word)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onAddClick(text) }) {
                    Text(stringResource(R.string.add))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismissRequest()
                    text = ""
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWordsBadgeButton(
    count: Int,
    onClick: () -> Unit
) {
    if (count > 0) {
        IconButton(onClick = onClick) {
            BadgedBox(
                badge = {
                    Badge { Text(count.toString()) }
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

@Composable
private fun VocabularyFloatingButton(onAddWordClick: () -> Unit, onTextAddingClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.AddCircleOutline, contentDescription = "Add Word")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onAddWordClick()
                },
                text = { Text("Add word") }
            )
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onTextAddingClick()
                },
                text = { Text("Add words with text") }
            )
        }
    }
}