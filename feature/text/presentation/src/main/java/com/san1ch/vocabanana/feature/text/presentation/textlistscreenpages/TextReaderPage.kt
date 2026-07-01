package com.san1ch.vocabanana.feature.text.presentation.textlistscreenpages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.san1ch.vocabanana.core.essentials.model.ReaderSettings
import com.san1ch.vocabanana.core.ui.TextUi
import com.san1ch.vocabanana.feature.text.presentation.R
import com.san1ch.vocabanana.feature.text.presentation.TextListUiIntent
import com.san1ch.vocabanana.feature.text.presentation.WordInfoState
import com.san1ch.vocabanana.feature.text.presentation.data.TextToken
import com.san1ch.vocabanana.feature.text.presentation.data.tokenize


@Composable
fun TextReaderPage(
    text: TextUi?,
    settings: ReaderSettings,
    onIntent: (TextListUiIntent) -> Unit
) {
    val listState = rememberLazyListState()
    var isScrollRestored by remember(text?.id) { mutableStateOf(false) }

    LaunchedEffect(text?.id, text?.paragraphs?.size) {
        if (text != null && text.paragraphs.isNotEmpty() && !isScrollRestored) {
            val savedProgress = text.lastScrollPosition
            if (savedProgress > 0f) {
                val targetIndex = (savedProgress * text.paragraphs.size).toInt()
                listState.scrollToItem(targetIndex.coerceIn(0, text.paragraphs.size - 1))
            }
            isScrollRestored = true
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (text != null && isScrollRestored) {
            val totalItems = listState.layoutInfo.totalItemsCount
            if (totalItems > 0) {
                val progress = listState.firstVisibleItemIndex.toFloat() / totalItems
                onIntent(TextListUiIntent.UpdateProgress(text.id, progress))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (text == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                // Apply dynamic padding from settings
                contentPadding = PaddingValues(
                    horizontal = settings.horizontalPadding.dp,
                    vertical = 16.dp
                )
            ) {
                items(text.paragraphs) { paragraph ->
                    ParagraphViewItem(
                        paragraphText = paragraph,
                        settings = settings,
                        onWordClick = { word -> onIntent(TextListUiIntent.WordClicked(word)) }
                    )
                    Spacer(modifier = Modifier.height(settings.paragraphSpacing.dp))
                }
            }
        }
    }
}

@Composable
fun ParagraphViewItem(
    paragraphText: String,
    settings: ReaderSettings,
    onWordClick: (String) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val highlightColor = MaterialTheme.colorScheme.primary

    val annotatedString = remember(paragraphText, textColor, highlightColor) {
        buildAnnotatedString {
            val tokens = paragraphText.tokenize()
            tokens.forEach { token ->
                when (token) {
                    is TextToken.Word -> {
                        withLink(
                            LinkAnnotation.Clickable(
                                tag = "WORD",
                                styles = TextLinkStyles(
                                    style = SpanStyle(color = textColor),
                                    pressedStyle = SpanStyle(
                                        color = highlightColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                ),
                                linkInteractionListener = { onWordClick(token.text) }
                            )
                        ) { append(token.text) }
                    }

                    is TextToken.Symbol -> append(token.text)
                }
            }
        }
    }

    Text(
        text = annotatedString,
        // Apply dynamic font size and line height
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = settings.fontSize.sp,
            lineHeight = (settings.fontSize * 1.5).sp
        ),
        modifier = Modifier.fillMaxWidth()
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
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { -100 }),
        exit = fadeOut(animationSpec = tween(250)) + slideOutVertically(targetOffsetY = { -100 })
    ) {
        // Backdrop: Uses a darker, more professional dim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Darker for better focus
                .pointerInput(Unit) { detectTapGestures { onDismiss() } }
                .padding(top = 56.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) { /* Stop click propagation */ }
                    .graphicsLayer {
                        clip = true
                        shape = RoundedCornerShape(24.dp)
                        shadowElevation = 20.dp.toPx()
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    when (state) {
                        is WordInfoState.Loading -> {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(strokeWidth = 3.dp)
                            }
                        }

                        is WordInfoState.NotFound -> {
                            Text(
                                "Definition Missing",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                "We couldn't find '${state.word}' in your local dictionary.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            ActionSection(state.word, onOxfordClick)
                        }

                        is WordInfoState.Found -> {
                            // Header Row: Word + Part of Speech Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = state.word.lemma,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (state.word.forms.isNotEmpty()) {
                                        Text(
                                            text = state.word.forms.joinToString(" • "),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }

                                // Clean POS Badge
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ) {
                                    Text(
                                        text = state.word.partOfSpeech.lowercase(),
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 4.dp
                                        ),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Definition with better line height
                            Text(
                                text = state.word.definition.ifEmpty { "No definition available." },
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 26.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            Spacer(Modifier.height(24.dp))
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
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Oxford Dictionary", fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


@Composable
fun ReaderSettingsPanel(
    visibility: Boolean,
    settings: ReaderSettings,
    onIntent: (TextListUiIntent) -> Unit
) {
    AnimatedVisibility(
        visible = visibility,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(28.dp),
            // Use a higher elevation or a specific container color for better contrast
            color = MaterialTheme.colorScheme.secondaryContainer,
            tonalElevation = 8.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Tighter vertical spacing
            ) {
                Text(
                    text = stringResource(R.string.appearance),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                SettingRow(stringResource(R.string.font_size), settings.fontSize) {
                    onIntent(
                        TextListUiIntent.ChangePageSettings(
                            settings.copy(
                                fontSize = it.coerceIn(
                                    12,
                                    36
                                )
                            )
                        )
                    )
                }

                SettingRow(stringResource(R.string.line_spacing), settings.paragraphSpacing) {
                    onIntent(
                        TextListUiIntent.ChangePageSettings(
                            settings.copy(
                                paragraphSpacing = it.coerceIn(
                                    0,
                                    64
                                )
                            )
                        )
                    )
                }

                SettingRow(stringResource(R.string.side_margins), settings.horizontalPadding) {
                    onIntent(
                        TextListUiIntent.ChangePageSettings(
                            settings.copy(
                                horizontalPadding = it.coerceIn(
                                    0,
                                    48
                                )
                            )
                        )
                    )
                }

                TextButton(
                    onClick = { onIntent(TextListUiIntent.CloseReaderSettings) },
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(32.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Text("Done", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f), // Subtle contrast
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        // Compact Control Block
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilledIconButton(
                onClick = { onValueChange(value - 2) },
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Value label stays right between the buttons
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            FilledIconButton(
                onClick = { onValueChange(value + 2) },
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}