package com.san1ch.vocabanana.feature.text.presentation.textlist.page.reading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.san1ch.vocabanana.core.essentials.model.text.TextWordCount
import com.san1ch.vocabanana.core.essentials.model.text.WordWithCount
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.ui.model.toText
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.WordInfoState
import kotlin.text.ifEmpty

@Composable
fun WordInfoPopup(
    state: WordInfoState,
    onDismiss: () -> Unit,
    onStateUpdate: (Int, WordState) -> Unit,
    onOxfordClick: (String) -> Unit,
) {
    AnimatedVisibility(
        visible = state !is WordInfoState.Hidden,
        enter = fadeIn() + slideInVertically { -50 },
        exit = fadeOut() + slideOutVertically { -50 },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .pointerInput(Unit) { detectTapGestures { onDismiss() } }
                .padding(20.dp)
                .statusBarsPadding(),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    when (state) {
                        is WordInfoState.Loading -> LoadingContent()
                        is WordInfoState.NotFound -> NotFoundContent(
                            state = state,
                            onOxfordClick = onOxfordClick,
                        )

                        is WordInfoState.Found -> FoundContent(
                            state = state,
                            onOxfordClick = onOxfordClick,
                            onStateSelected = { newState -> onStateUpdate(state.word.id, newState) },
                        )

                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
            .size(40.dp),
    )
}

@Composable
private fun NotFoundContent(
    state: WordInfoState.NotFound,
    onOxfordClick: (String) -> Unit,
) {
    Column {
        Text(
            text = "Not found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "The word '${state.word}' is missing from your dictionary.",
            style = MaterialTheme.typography.bodyMedium,
        )
        ActionSection(word = state.word, onOxfordClick = onOxfordClick)
    }
}

@Composable
private fun FoundContent(
    state: WordInfoState.Found,
    onOxfordClick: (String) -> Unit,
    onStateSelected: (WordState) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    Column {
        // Header: Lemma + POS (Compact)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = state.word.lemma,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${state.word.partOfSpeech.uppercase()} • ${state.wordCountInTheText.count} in this text (${state.word.count} total)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            // Subtle badge
            Box {
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            CircleShape,
                        )
                        .clickable { showMenu = true }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = state.word.state.toText().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    WordState.entries.forEach { wordState ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = wordState.toText().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                            onClick = {
                                showMenu = false
                                onStateSelected(wordState)
                            },
                        )
                    }
                }
            }
        }
    }

    Spacer(Modifier.height(12.dp))

    Text(
        text = state.word.definition.ifEmpty { "No definition." },
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    ActionSection(word = state.word.lemma, onOxfordClick = onOxfordClick)
}


@Composable
private fun ActionSection(word: String, onOxfordClick: (String) -> Unit) {
    Spacer(Modifier.height(16.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    Spacer(Modifier.height(12.dp))
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        TextButton(
            onClick = { onOxfordClick(word) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
        ) {
            Text("Oxford Dictionary", fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}