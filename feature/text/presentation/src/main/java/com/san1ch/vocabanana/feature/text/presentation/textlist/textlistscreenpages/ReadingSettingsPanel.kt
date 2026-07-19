package com.san1ch.vocabanana.feature.text.presentation.textlist.textlistscreenpages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.feature.text.presentation.R
import com.san1ch.vocabanana.feature.text.presentation.textlist.TextListUiIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsPanel(
    visibility: Boolean,
    settings: TextAppearanceSettings,
    selectedStates: Set<WordState>,
    onIntent: (TextListUiIntent) -> Unit,
    onStatesSave: (Set<WordState>) -> Unit,
) {
    if (visibility) {
        var currentTab by remember { mutableStateOf(0) }

        ModalBottomSheet(
            onDismissRequest = {
                onIntent(TextListUiIntent.Navigation.CloseReaderSettings)
            },
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp),
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn(
                            animationSpec = tween(200),
                        ) togetherWith fadeOut(
                            animationSpec = tween(150),
                        ) using SizeTransform(
                            clip = false,
                            sizeAnimationSpec = { _, _ ->
                                tween(300)
                            },
                        )
                    },
                    label = "settings_content",
                ) { tab ->
                    when (tab) {
                        0 -> {
                            DisplaySettingsContent(
                                settings = settings,
                                onIntent = onIntent,
                            )
                        }

                        1 -> {
                            FilterSettingsContent(
                                selectedStates = selectedStates,
                                onSave = onStatesSave,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TabButton(
                        modifier = Modifier.weight(1f),
                        text = "Display",
                        isSelected = currentTab == 0,
                        onClick = { currentTab = 0 },
                    )

                    TabButton(
                        modifier = Modifier.weight(1f),
                        text = "Filters",
                        isSelected = currentTab == 1,
                        onClick = { currentTab = 1 },
                    )
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                Color.Transparent
            },
        ),
    ) {
        Text(
            text = text,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.Gray
            },
        )
    }
}

@Composable
private fun DisplaySettingsContent(
    settings: TextAppearanceSettings,
    onIntent: (TextListUiIntent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingRow(stringResource(R.string.font_size), settings.fontSize) {
            onIntent(
                TextListUiIntent.Reader.ChangePageSettings(
                    settings.copy(
                        fontSize = it.coerceIn(
                            12,
                            36,
                        ),
                    ),
                ),
            )
        }
        SettingRow(stringResource(R.string.line_spacing), settings.lineSpacing) {
            onIntent(
                TextListUiIntent.Reader.ChangePageSettings(
                    settings.copy(
                        lineSpacing = it.coerceIn(
                            0,
                            24,
                        ),
                    ),
                ),
            )
        }
        SettingRow(stringResource(R.string.paragraph_spacing), settings.paragraphSpacing) {
            onIntent(
                TextListUiIntent.Reader.ChangePageSettings(
                    settings.copy(
                        paragraphSpacing = it.coerceIn(
                            0,
                            64,
                        ),
                    ),
                ),
            )
        }
        SettingRow(stringResource(R.string.side_margins), settings.horizontalPadding) {
            onIntent(
                TextListUiIntent.Reader.ChangePageSettings(
                    settings.copy(
                        horizontalPadding = it.coerceIn(
                            0,
                            48,
                        ),
                    ),
                ),
            )
        }
    }
}

@Composable
private fun FilterSettingsContent(
    selectedStates: Set<WordState>,
    onSave: (Set<WordState>) -> Unit,
) {
    var activatedStates by remember(selectedStates) { mutableStateOf(selectedStates) }

    val isChanged = selectedStates != activatedStates

    Column(modifier = Modifier.wrapContentHeight().fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            WordState.entries.forEach { state ->
                FilterChip(
                    modifier = Modifier.fillMaxWidth(),
                    selected = activatedStates.contains(state),
                    onClick = {
                        activatedStates = if (activatedStates.contains(state)) {
                            activatedStates - state
                        } else {
                            activatedStates + state
                        }
                    },
                    label = { Text(state.name.lowercase().replaceFirstChar { it.uppercase() }) },
                )
            }
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSave(activatedStates) },
            enabled = isChanged,
            border = if (isChanged) null else BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isChanged) MaterialTheme.colorScheme.primary else Color.Transparent,
                contentColor = if (isChanged) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text("Save")
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilledIconButton(
                onClick = { onValueChange(value - 2) },
                modifier = Modifier.size(28.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Icon(Icons.Default.Remove, null, modifier = Modifier.size(12.dp))
            }

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center,
            )

            FilledIconButton(
                onClick = { onValueChange(value + 2) },
                modifier = Modifier.size(28.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(12.dp))
            }
        }
    }
}
