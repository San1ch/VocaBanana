package com.san1ch.vocabanana.feature.text.presentation.textlistscreenpages

import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.san1ch.vocabanana.core.ui.model.TextPreview
import com.san1ch.vocabanana.core.ui.toFormattedDate
import com.san1ch.vocabanana.feature.text.presentation.R
import com.san1ch.vocabanana.feature.text.presentation.TextListUiIntent

@Composable
fun TextListPage(
    items: List<TextPreview>,
    onIntent: (TextListUiIntent) -> Unit,
    onNavigateToReader: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items, key = { it.id }) { item ->
            Box(
                modifier = Modifier.animateItem(
                    placementSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
                ),
            ) {
                TextLazyItem(
                    item = item,
                    onClick = {
                        onIntent(TextListUiIntent.SelectText(item.id))
                        onNavigateToReader()
                    },
                    onDelete = {
                        onIntent(TextListUiIntent.SelectTextToDelete(item.id))
                    },
                )
            }
        }
    }
}

@Composable
private fun TextLazyItem(
    item: TextPreview,
    onClick: () -> Unit,
    onDelete: (TextPreview) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp) // Added breathing room
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.surface
            } else {
                Color.White
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        ),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min), // Essential for the vertical bar
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 1. Accent Bar - Using Secondary to distinguish from Vocabulary
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.secondary),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 2. Metadata: Labeling the date for better clarity
                Text(
                    text = stringResource(R.string.last_read, item.lastReadTime.toFormattedDate()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // 3. Delete Action - Subtle so it doesn't distract
            IconButton(
                onClick = { onDelete(item) },
                modifier = Modifier.padding(end = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
