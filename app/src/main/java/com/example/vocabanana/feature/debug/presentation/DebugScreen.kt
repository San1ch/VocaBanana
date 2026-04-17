package com.example.vocabanana.feature.debug.presentation


import android.R.attr.text
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.feature.text.presentation.data.TextPreview
import com.example.vocabanana.ui.composable.CollectUiEvents


@Composable
fun DebugScreen(
    viewModel: DebugScreenViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateTo: (AppDestination) -> Unit
) {
    val textsState by viewModel.textsState.collectAsState()
    val selectedId by viewModel.selectedTextId.collectAsStateWithLifecycle()

    val finished by viewModel.finished.collectAsState()

    CollectUiEvents(
        viewModel.events,
        navigateBack = navigateBack,
        navigateTo = navigateTo
    )

    StateObserver(textsState) { texts ->
        DebugContent(
            texts = texts,
            selectedId = selectedId,
            onAction = viewModel::handleAction,
            finished = finished
        )
    }
}

@Composable
fun DebugContent(
    texts: List<TextPreview>,
    selectedId: Int?,
    onAction: (DebugAction) -> Unit,
    finished: Boolean?
) {
    var textState by remember { mutableStateOf("") }
    textState = when(finished){
        true -> "All unknown words were added"
        false -> "Analyzing in progress"
        null -> "Analyze wasn't started"
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(texts) { text ->
                FilterChip(
                    selected = selectedId == text.id,
                    onClick = { onAction(DebugAction.SelectText(text.id)) },
                    label = { Text(text.title) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(text = textState)

        Button(onClick = { onAction(DebugAction.AnalyzeUnknowns) }) {
            Text("Analyze unknowns words")
        }

    }
}