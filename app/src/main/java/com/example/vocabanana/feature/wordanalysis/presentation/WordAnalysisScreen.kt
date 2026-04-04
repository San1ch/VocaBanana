package com.example.vocabanana.feature.wordanalysis.presentation


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.feature.text.presentation.data.TextPreview
import com.example.vocabanana.feature.wordanalysis.domain.TestWordCorrectionStage
import com.example.vocabanana.ui.composable.CollectUiEvents


@Composable
fun WordAnalysisScreen(
    viewModel: WordAnalysisScreenViewModel = hiltViewModel(),
) {

    val textState = viewModel.text.collectAsState()
    val textStage = viewModel.testStage.collectAsState(TestWordCorrectionStage.Sleep)
    StateObserver(textState.value) { texts ->
        WordAnalysisContent(
            texts = texts,
            testStage = textStage.value,
            startTest = { textId -> viewModel.startTest(textId) }
        )
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WordAnalysisContent(
    texts: List<TextPreview>,
    testStage: TestWordCorrectionStage,
    startTest: (Int) -> Unit
) {
    val selectedTextId = remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(texts) { text ->
                    TextCard(
                        title = text.title,
                        onClick = {
                            selectedTextId.value = text.id
                        },
                        isSelected = selectedTextId.value == text.id
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column() {

                when (testStage) {
                    TestWordCorrectionStage.Sleep -> Text("Sleeping...")
                    is TestWordCorrectionStage.Working -> {
                        Text("Batch stage: ${testStage.currentBatchStage}/${testStage.maxBatchStage}")
                        Text("Current prompt: ${testStage.currentBatchStage}/${testStage.maxBatchStage}")
                    }
                }
            }
            when (selectedTextId.value) {
                null -> {
                    Text(
                        text = "No text selected",
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    Button(
                        onClick = { startTest(selectedTextId.value!!)}
                    ) { }
                }
            }
        }
    }
}

@Composable
fun TextCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null,
        modifier = Modifier.height(60.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.labelLarge)
        }
    }
}