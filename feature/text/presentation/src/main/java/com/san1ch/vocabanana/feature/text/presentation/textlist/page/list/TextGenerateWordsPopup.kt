package com.san1ch.vocabanana.feature.text.presentation.textlist.page.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.san1ch.vocabanana.core.ui.compose.AppBasePopup
import com.san1ch.vocabanana.feature.text.presentation.R
import com.san1ch.vocabanana.feature.text.presentation.model.GenerateWordsFromTextUiState
import com.san1ch.vocabanana.feature.text.presentation.textlist.viewmodel.TextListUiIntent

@Composable
fun TextGenerateWordsPopup(
    textId: Int?,
    generatingState: GenerateWordsFromTextUiState?,
    onDismiss: () -> Unit,
    onIntent: (TextListUiIntent) -> Unit,
) {
    LaunchedEffect(textId) {
        if (textId != null) {
            onIntent(TextListUiIntent.Dictionary.GenerateWords(textId))
        }
    }

    AppBasePopup(
        visible = textId != null,
        onDismiss = onDismiss,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.vocabulary_generator),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.extract_and_save_new_words_from_this_text_to_your_local_dictionary),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }

            if (generatingState is GenerateWordsFromTextUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp,
                )
            }

            AnimatedVisibility(
                visible = generatingState != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                GeneratingWordStatus(generatingState)
            }
        }
    }
}

@Composable
private fun GeneratingWordStatus(state: GenerateWordsFromTextUiState?) {
    if (state == null) return

    val (color, icon, bg) = when (state) {
        is GenerateWordsFromTextUiState.Success -> Triple(
            Color(0xFF4CAF50),
            Icons.Default.CheckCircle,
            Color(0xFF4CAF50).copy(0.1f),
        )

        is GenerateWordsFromTextUiState.Error -> Triple(
            MaterialTheme.colorScheme.error,
            Icons.Default.Error,
            MaterialTheme.colorScheme.error.copy(0.1f),
        )

        is GenerateWordsFromTextUiState.Loading -> Triple(
            MaterialTheme.colorScheme.primary,
            Icons.Default.Sync,
            MaterialTheme.colorScheme.primary.copy(0.1f),
        )
    }

    val message = when (state) {
        is GenerateWordsFromTextUiState.Loading -> state.message
        is GenerateWordsFromTextUiState.Success -> state.message
        is GenerateWordsFromTextUiState.Error -> state.message
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = color,
                modifier = Modifier.weight(1f),
            )
        }
    }
}