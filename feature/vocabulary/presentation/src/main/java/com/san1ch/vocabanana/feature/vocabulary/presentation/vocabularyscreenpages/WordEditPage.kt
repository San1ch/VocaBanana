package com.san1ch.vocabanana.feature.vocabulary.presentation.vocabularyscreenpages

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.san1ch.vocabanana.core.essentials.model.word.PartOfSpeech
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.ui.model.WordUi
import com.san1ch.vocabanana.feature.vocabulary.presentation.R
import com.san1ch.vocabanana.feature.vocabulary.presentation.VocabularyIntent
import com.san1ch.vocabanana.feature.vocabulary.presentation.VocabularyScreenViewModel
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WordDetailsAndEditPage(
    word: WordUi?,
    onUpdateWord: (WordUi) -> Unit,
    viewModel: VocabularyScreenViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(word?.lemma ?: "Word Details") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onIntent(VocabularyIntent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
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
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp), // Компактніші відступи
            ) {
                // Header with Lemma and Frequency
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(text = word.lemma, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Used ${word.count} times in your texts",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // 1. Learning Status: Used a more compact layout
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.learning_status), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WordState.entries.forEach { state ->
                            val isSelected = word.state == state
                            FilterChip(
                                selected = isSelected,
                                onClick = { onUpdateWord(word.copy(state = state)) },
                                label = { Text(state.name.replace("_", " "), style = MaterialTheme.typography.labelMedium) },
                            )
                        }
                    }
                }

                // 2. Part of Speech Selection (Compact Dropdown)
                var posExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = posExpanded,
                    onExpandedChange = { posExpanded = !posExpanded },
                ) {
                    OutlinedTextField(
                        value = word.partOfSpeech.uppercase(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.part_of_speech)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = posExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    ExposedDropdownMenu(expanded = posExpanded, onDismissRequest = { posExpanded = false }) {
                        PartOfSpeech.entries.forEach { pos ->
                            DropdownMenuItem(
                                text = { Text(pos.name) },
                                onClick = {
                                    onUpdateWord(word.copy(partOfSpeech = pos.shortName))
                                    posExpanded = false
                                },
                            )
                        }
                    }
                }

                // 3. Definition (Less intrusive)
                OutlinedTextField(
                    value = word.definition,
                    onValueChange = { onUpdateWord(word.copy(definition = it)) },
                    label = { Text(stringResource(R.string.definition)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = MaterialTheme.shapes.medium,
                )

                // 4. Metadata
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoRow("Forms", word.forms.joinToString(", ").ifEmpty { "None" })
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
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
