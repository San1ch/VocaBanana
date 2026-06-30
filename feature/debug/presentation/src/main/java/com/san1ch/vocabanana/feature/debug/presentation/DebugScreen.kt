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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.san1ch.vocabanana.core.ui.StateObserver
import com.san1ch.vocabanana.core.ui.TextPreview
import com.san1ch.vocabanana.core.ui.compose.CollectUiEvents

sealed class DebugIntent {
    data class SelectText(val id: Int) : DebugIntent()
    object DeleteAllWords : DebugIntent()
}

@Composable
fun DebugScreen(
    viewModel: DebugScreenViewModel = hiltViewModel()
) {
    val textsState by viewModel.textsState.collectAsState()
    val selectedId by viewModel.selectedTextId.collectAsStateWithLifecycle()


    CollectUiEvents(
        viewModel.events
    )

    StateObserver(
        state = textsState,
        onSuccess = { texts ->
            DebugContent(
                texts = texts,
                selectedId = selectedId,
                onIntent = viewModel::handleAction
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
            Text("Delete All Words")
        }
    }
}