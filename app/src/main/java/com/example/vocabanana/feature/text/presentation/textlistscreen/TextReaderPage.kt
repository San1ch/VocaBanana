package com.example.vocabanana.feature.text.presentation.textlistscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.example.vocabanana.feature.text.presentation.TextListUiIntent
import com.example.vocabanana.feature.text.presentation.data.TextUi
import com.example.vocabanana.ui.composable.DpSizes

@Composable
fun TextReaderPage(
    text: TextUi?,
    onIntent: (TextListUiIntent) -> Unit // MVI Intent handler
) {
    val listState = rememberLazyListState()
    // Local UI state for scroll restoration is okay to keep here
    // as it is "View-specific" logic.
    var isScrollRestored by remember(text?.id) { mutableStateOf(false) }

    // 1. Restore scroll position when text is loaded
    LaunchedEffect(text?.id, text?.paragraphs?.size) {
        val savedProgress = text?.lastScrollPosition
        val totalItems = text?.paragraphs?.size ?: 0

        if (totalItems > 0 && !isScrollRestored) {
            if (savedProgress != null && savedProgress > 0f) {
                val targetIndex = (savedProgress * totalItems).toInt()
                listState.scrollToItem(targetIndex)
            }
            isScrollRestored = true
        }
    }

    // 2. Report progress changes via Intents
    LaunchedEffect(listState.firstVisibleItemIndex) {
        val id = text?.id
        val totalItems = listState.layoutInfo.totalItemsCount
        if (id != null && totalItems > 0 && isScrollRestored) {
            val progress = listState.firstVisibleItemIndex.toFloat() / totalItems
            // Send the intent to the ViewModel
            onIntent(TextListUiIntent.UpdateProgress(id, progress))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (text == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(DpSizes.small)
                    .graphicsLayer { alpha = if (isScrollRestored) 1f else 0f }
            ) {
                items(text.paragraphs) { paragraph ->
                    ParagraphViewItem(
                        paragraph = paragraph,
                        // Send word click intent
                        onWordClick = { word ->
                            onIntent(TextListUiIntent.WordClicked(word))
                        }
                    )
                }
            }

            if (!isScrollRestored) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
