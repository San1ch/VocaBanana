@file:Suppress("DEPRECATION")

package com.example.vocabanana.feature.text.presentation.textlistscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.feature.text.presentation.TextListScreenViewModel
import com.example.vocabanana.feature.text.presentation.TextListUiIntent
import com.example.vocabanana.feature.text.presentation.data.ParagraphUi
import com.example.vocabanana.feature.text.presentation.data.TextPreview
import com.example.vocabanana.feature.text.presentation.data.TextToken
import com.example.vocabanana.feature.text.presentation.data.WordUi
import com.example.vocabanana.feature.text.presentation.data.tokenize
import com.example.vocabanana.ui.composable.CollectUiEvents
import com.example.vocabanana.ui.composable.DeleteConfirmDialog

sealed class WordInfoState {
    object Hidden : WordInfoState()
    object Loading : WordInfoState()
    data class Found(val word: WordUi) : WordInfoState()
    data class NotFound(val word: String) : WordInfoState()
}


@Composable
fun TextListScreen(
    viewModel: TextListScreenViewModel = hiltViewModel(),
    navigateTo: (AppDestination) -> Unit,
    navigateBack: () -> Unit,
) {
    // 1. Observe the single MVI state
    val state by viewModel.uiState.collectAsState()
    var deletingText by remember { mutableStateOf<TextPreview?>(null) }

    // 2. Handle simple local UI state (Dialogs)
    DeleteConfirmDialog(
        item = deletingText,
        onDismiss = { deletingText = null },
        onConfirm = { text ->
            viewModel.onIntent(TextListUiIntent.DeleteText(text))
            deletingText = null
        }
    )

    CollectUiEvents(events = viewModel.events, navigateBack = navigateBack, navigateTo = {})

    // 3. Pass the state and the intent handler down
    TextListContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}




@Composable
fun ParagraphViewItem(paragraph: ParagraphUi, onWordClick: (String) -> Unit) {
    val textColor = MaterialTheme.colorScheme.onPrimary

    val annotatedString = remember(paragraph.rawText) {
        buildAnnotatedString {
            val tokens = paragraph.rawText.tokenize()
            tokens.forEach { token ->
                if (token is TextToken.Word) {
                    pushStringAnnotation(tag = "WORD", annotation = token.text)
                    // Change 'color = color' to 'textColor' or keep it subtle
                    withStyle(style = SpanStyle(color = textColor, fontWeight = FontWeight.Normal)) {
                        append(token.text)
                    }
                    pop()
                } else if (token is TextToken.Symbol) {
                    append(token.text)
                }
            }
        }
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "WORD", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onWordClick(annotation.item)
                }
        }
    )
}

@Composable
fun WordInfoPopup(
    state: WordInfoState,
    onDismiss: () -> Unit,
    onOxfordClick: (String) -> Unit
) {
    AnimatedVisibility(
        visible = state !is WordInfoState.Hidden,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        // Full screen transparent box to catch clicks outside
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = true, onClick = onDismiss)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable(enabled = false) {},
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    when (state) {
                        is WordInfoState.Loading -> CircularProgressIndicator(Modifier.size(24.dp))

                        is WordInfoState.NotFound -> {
                            Text("Word not in vocabulary", fontWeight = FontWeight.Bold)
                            OxfordButton(state.word, onOxfordClick)
                        }

                        is WordInfoState.Found -> {
                            // Lemma and POS
                            Text(
                                text = "${state.word.lemma} (${state.word.partOfSpeech})",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Forms joined by comma
                            if (state.word.forms.isNotEmpty()) {
                                Text(
                                    text = state.word.forms.joinToString(", "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            // Definition
                            Text(
                                text = state.word.definition.ifEmpty { "No definition found for this word." },
                                style = MaterialTheme.typography.bodyMedium
                            )

                            OxfordButton(state.word.lemma, onOxfordClick)
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun OxfordButton(word: String, onClick: (String) -> Unit) {
    TextButton(
        onClick = { onClick(word) },
        contentPadding = PaddingValues(0.dp)
    ) {
        Text("Oxford Dictionary →", style = MaterialTheme.typography.labelSmall)
    }
}

