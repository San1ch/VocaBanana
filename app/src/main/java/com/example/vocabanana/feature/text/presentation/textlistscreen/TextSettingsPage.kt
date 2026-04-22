package com.example.vocabanana.feature.text.presentation.textlistscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vocabanana.feature.text.presentation.TextListUiIntent
import com.example.vocabanana.feature.text.presentation.data.GenerateWordsFromTextUiState


@Composable
fun TextSettingsPage(
    generatingState: GenerateWordsFromTextUiState?,
    onIntent: (TextListUiIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Vocabulary Generator",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { onIntent(TextListUiIntent.GenerateWords) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    enabled = generatingState !is GenerateWordsFromTextUiState.Loading
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Generate Words", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        AnimatedVisibility(
            visible = generatingState != null,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            GeneratingWordStatus(generatingState)
        }
    }
}


@Composable
private fun GeneratingWordStatus(state: GenerateWordsFromTextUiState?) {
    if (state == null) return

    val context = LocalContext.current

    val message = when (state) {
        is GenerateWordsFromTextUiState.Loading -> state.message.asString(context)
        is GenerateWordsFromTextUiState.Success -> state.message.asString(context)
        is GenerateWordsFromTextUiState.Error -> state.message.asString(context)
        is GenerateWordsFromTextUiState.AllExist -> state.message.asString(context)
        is GenerateWordsFromTextUiState.PartialSuccess -> state.message.asString(context)
    }

    val (color, icon) = when (state) {
        is GenerateWordsFromTextUiState.Success -> Color(0xFF4CAF50) to Icons.Default.CheckCircle
        is GenerateWordsFromTextUiState.PartialSuccess -> Color(0xFFFF9800) to Icons.Default.Warning
        is GenerateWordsFromTextUiState.Error -> MaterialTheme.colorScheme.error to Icons.Default.Warning
        is GenerateWordsFromTextUiState.Loading -> MaterialTheme.colorScheme.primary to Icons.Default.Refresh
        is GenerateWordsFromTextUiState.AllExist -> MaterialTheme.colorScheme.secondary to Icons.Default.Info
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            textAlign = TextAlign.Center
        )

        if (state is GenerateWordsFromTextUiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        }
    }
}
