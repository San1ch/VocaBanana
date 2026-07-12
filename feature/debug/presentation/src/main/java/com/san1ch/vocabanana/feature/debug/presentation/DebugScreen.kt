package com.san1ch.vocabanana.feature.debug.presentation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.san1ch.vocabanana.core.ui.state.ResourceObserver
import com.san1ch.vocabanana.core.ui.model.TextPreview
import com.san1ch.vocabanana.core.ui.compose.CollectResource

sealed class DebugIntent {
    data class SelectText(val id: Int) : DebugIntent()
    object DeleteAllWords : DebugIntent()
    object PrintWords : DebugIntent()

    object PrintWordCounts : DebugIntent()
}

@Composable
fun DebugScreen(
    viewModel: DebugScreenViewModel = hiltViewModel()
) {
    val textsState by viewModel.textsState.collectAsState()
    val selectedId by viewModel.selectedTextId.collectAsStateWithLifecycle()


    CollectResource(
        viewModel.events
    )

    ResourceObserver(
        state = textsState,
        onSuccess = { texts ->
            DebugContent(
                texts = texts,
                selectedId = selectedId,
                onIntent = viewModel::onIntent
            )
        }
    )
}

@Composable
fun DebugContent(
    texts: List<TextPreview>,
    selectedId: Int?,
    onIntent: (DebugIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(texts) { text: TextPreview ->
                FilterChip(
                    selected = selectedId == text.id,
                    onClick = { onIntent(DebugIntent.SelectText(text.id)) },
                    label = { Text(text.title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onIntent(DebugIntent.DeleteAllWords) }) {
            Text(stringResource(R.string.delete_all_words))
        }
        Button(
            onClick = { onIntent(DebugIntent.PrintWords) }
        ) {
            Text("Print words")
        }

        Button(
            onClick = { onIntent(DebugIntent.PrintWordCounts) }
        ) {
            Text("Print counts")
        }
    }
}