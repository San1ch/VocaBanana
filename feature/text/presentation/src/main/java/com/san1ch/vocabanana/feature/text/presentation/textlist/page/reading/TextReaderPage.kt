package com.san1ch.vocabanana.feature.text.presentation.textlist.page.reading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.san1ch.vocabanana.feature.text.presentation.data.TextToken
import com.san1ch.vocabanana.feature.text.presentation.data.toReadingStateColor
import com.san1ch.vocabanana.feature.text.presentation.model.TextWithContent
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiIntent
import kotlin.math.abs

@Composable
fun TextReaderPage(
    text: TextWithContent,
    onIntent: (TextListUiIntent) -> Unit,
) {
    // Restore scroll position
    val initialIndex = remember(text.id) {
        val savedProgress = text.lastScrollPosition ?: 0f
        val targetIndex = (savedProgress * text.content.size).toInt()
        targetIndex.coerceIn(0, maxOf(0, text.content.size - 1))
    }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex,
    )

    // Save progress
    LaunchedEffect(listState.firstVisibleItemIndex) {
        val progress = listState.firstVisibleItemIndex.toFloat() / text.content.size

        val currentProgress = text.lastScrollPosition ?: 0f
        if (abs(progress - currentProgress) > 0.001f) {
            onIntent(TextListUiIntent.Reader.UpdateProgress(text.id, progress))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val settings = text.textAppearanceSettings

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = settings.horizontalPadding.dp,
                vertical = 16.dp,
            ),
        ) {
            items(text.content) { paragraph ->
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
                    currentActiveState = text.activeWordStates,
                )
                Spacer(modifier = Modifier.height(settings.paragraphSpacing.dp))
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
                                    style = SpanStyle(
                                        color = stateColor,
                                        fontWeight = if (isStateActive) FontWeight.Bold else null,
                                    ),
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
