package com.san1ch.vocabanana.feature.vocabulary.presentation.vocabularyscreenpages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.san1ch.vocabanana.core.essentials.model.word.PartOfSpeech
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.ui.WordUi
import com.san1ch.vocabanana.feature.vocabulary.presentation.VocabularyIntent
import com.san1ch.vocabanana.feature.vocabulary.presentation.VocabularyScreenViewModel
import java.text.SimpleDateFormat


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WordDetailsAndEditPage(
    word: WordUi?,
    onUpdateWord: (WordUi) -> Unit,
    viewModel: VocabularyScreenViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(word?.lemma ?: "Word Details") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onIntent(VocabularyIntent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (word == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Select a word to view details", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Lemma Display
                Text(
                    text = word.lemma,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )

                // 1. Status Selection (Chips)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Learning Status", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WordState.entries.forEach { state ->
                            FilterChip(
                                selected = word.state == state,
                                onClick = { onUpdateWord(word.copy(state = state)) },
                                label = { Text(state.name) }
                            )
                        }
                    }
                }

                // 2. Part of Speech Selection
                var posExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = posExpanded,
                    onExpandedChange = { posExpanded = !posExpanded }
                ) {
                    OutlinedTextField(
                        value = word.partOfSpeech.uppercase(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Part of Speech") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = posExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = posExpanded,
                        onDismissRequest = { posExpanded = false }
                    ) {
                        PartOfSpeech.entries.forEach { pos ->
                            DropdownMenuItem(
                                text = { Text(pos.name) },
                                onClick = {
                                    onUpdateWord(word.copy(partOfSpeech = pos.shortName))
                                    posExpanded = false
                                }
                            )
                        }
                    }
                }

                // 3. Editable Definition
                OutlinedTextField(
                    value = word.definition,
                    onValueChange = { onUpdateWord(word.copy(definition = it)) },
                    label = { Text("Definition") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // 4. Metadata (Non-editable)
                HorizontalDivider()

                InfoRow("Word Forms", word.forms.joinToString(", ").ifEmpty { "None" })

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoRow("Added", SimpleDateFormat("MMM dd, yyyy").format(word.whenAdded))
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
