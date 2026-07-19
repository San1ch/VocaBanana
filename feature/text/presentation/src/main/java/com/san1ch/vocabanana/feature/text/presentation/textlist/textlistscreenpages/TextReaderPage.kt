package com.san1ch.vocabanana.feature.text.presentation.textlist.textlistscreenpages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.ui.theme.LocalDarkTheme
import com.san1ch.vocabanana.feature.text.domain.model.TextListItem
import com.san1ch.vocabanana.feature.text.presentation.data.TextToken
import com.san1ch.vocabanana.feature.text.presentation.data.toReadingStateColor
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.textlist.WordInfoState

@Composable
fun TextReaderPage(
    text: TextListItem?,
    content: List<List<TextToken>>?,
    onIntent: (TextListUiIntent) -> Unit,
    currentActiveState: Set<WordState>,
) {
    val listState = rememberLazyListState()

    // Restore scroll position
    LaunchedEffect(text?.id) {
        if (text != null && content != null && content.isNotEmpty()) {
            val savedProgress = text.lastScrollPosition ?: 0f
            val targetIndex = (savedProgress * content.size).toInt()
            listState.scrollToItem(targetIndex.coerceIn(0, content.size - 1))
        }
    }

    // Save progress
    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (text != null && content != null && content.isNotEmpty()) {
            val progress = listState.firstVisibleItemIndex.toFloat() / content.size

            val currentProgress = text.lastScrollPosition ?: 0f
            if (kotlin.math.abs(progress - currentProgress) > 0.001f) {
                onIntent(TextListUiIntent.Reader.UpdateProgress(text.id, progress))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (text == null || content == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            val settings = text.textAppearanceSettings

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = settings.horizontalPadding.dp,
                    vertical = 16.dp,
                ),
            ) {
                items(content) { paragraph ->
                    ParagraphViewItem(
                        paragraphText = paragraph,
                        settings = settings,
                        onWordClick = { word ->
                            onIntent(
                                TextListUiIntent.Dictionary.WordClicked(
                                    word,
                                ),
                            )
                        },
                        currentActiveState = currentActiveState,
                    )
                    Spacer(modifier = Modifier.height(settings.paragraphSpacing.dp))
                }
            }
        }
    }
}

@Composable
fun ParagraphViewItem(
    paragraphText: List<TextToken>,
    settings: TextAppearanceSettings,
    onWordClick: (String) -> Unit,
    currentActiveState: Set<WordState>,
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val isLightTheme = !LocalDarkTheme.current
    println("isLight: $isLightTheme")
    val annotatedString = remember(currentActiveState, paragraphText, textColor, isLightTheme) {
        buildAnnotatedString {
            paragraphText.forEach { token ->
                when (token) {
                    is TextToken.Word -> {
                        val isStateActive = currentActiveState.contains(token.state)
                        val stateColor = if (isStateActive) {
                            token.state.toReadingStateColor(isLightTheme, textColor)
                        } else {
                            textColor
                        }
                        withLink(
                            LinkAnnotation.Clickable(
                                tag = "WORD",
                                styles = TextLinkStyles(
                                    style = SpanStyle(color = stateColor, fontWeight = if (isStateActive) FontWeight.Bold else null),
                                ),
                                linkInteractionListener = { onWordClick(token.text) },
                            ),
                        ) { append(token.text) }
                    }

                    is TextToken.Symbol -> append(token.text)
                }
            }
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = settings.fontSize.sp,
            lineHeight = (settings.fontSize + settings.lineSpacing).sp,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun WordInfoPopup(
    state: WordInfoState,
    onDismiss: () -> Unit,
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
                        is WordInfoState.Loading -> CircularProgressIndicator(
                            Modifier
                                .align(
                                    Alignment.CenterHorizontally,
                                )
                                .size(40.dp),
                        )

                        is WordInfoState.NotFound -> {
                            Text(
                                "Not found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                            Text(
                                "The word '${state.word}' is missing from your dictionary.",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            ActionSection(state.word, onOxfordClick)
                        }

                        is WordInfoState.Found -> {
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
                                    Text(
                                        text = "${state.word.partOfSpeech.uppercase()} • ${state.word.count} uses",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                                // Subtle badge
                                Text(
                                    text = state.word.state.name.lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            CircleShape,
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = state.word.definition.ifEmpty { "No definition." },
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            ActionSection(state.word.lemma, onOxfordClick)
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionSection(word: String, onOxfordClick: (String) -> Unit) {
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
