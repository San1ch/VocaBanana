package com.example.vocabanana.feature.vocabulary.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.BuildConfig
import com.example.vocabanana.R
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.ui.composable.SpacerMicro
import com.example.vocabanana.ui.theme.VocabBananaTheme

@Composable
fun VocabularyScreen(viewModel: VocabularyScreenViewModel = hiltViewModel()) {
    val words = viewModel.words.collectAsState()
    StateObserver(words.value) { words ->
        VocabularyContent(
            words = words,
            onMenuClick = {},
            onAllDelete = { viewModel.deleteAll() },
            onMoreClick = {},
            onAddWordClick = {},
            onTextAddingClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyContent(
    words: List<WordUi>,
    onMenuClick: () -> Unit = {},
    onAllDelete: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onAddWordClick: (String) -> Unit = {},
    onTextAddingClick: () -> Unit = {}
) {
    var addWordInlineDialog by remember { mutableStateOf(false) }

    AddWordInline(
        onAddClick = onAddWordClick,
        onDismissRequest = { addWordInlineDialog = false },
        showDialog = addWordInlineDialog,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.vocabulary)) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    if (BuildConfig.DEBUG) {
                        IconButton(onClick = onAllDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Debug")
                        }
                    }
                    VocabularyFilterButton()
                    IconButton(onClick = onMoreClick) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },

                )
        },
        floatingActionButton = {
            VocabularyFloatingButton(
                onAddWordClick = { addWordInlineDialog = true },
                onTextAddingClick = onTextAddingClick,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 16.dp
                    )
            ) {
                items(words.size) { wordId ->
                    val uiWord = words[wordId]
                    val form = uiWord.forms.firstOrNull()

                    Text(
                        "${uiWord.word} [${uiWord.partOfSpeech}]:"
                    )
                    form?.let {
                        Text("${it.form} [${it.partOfSpeech}]")
                    }
                    SpacerMicro()
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

@Preview
@Composable
fun VocabularyScreenPreview() {
    VocabBananaTheme(darkTheme = false) {
        VocabularyContent(
            listOf(

            )
        )
    }
}
