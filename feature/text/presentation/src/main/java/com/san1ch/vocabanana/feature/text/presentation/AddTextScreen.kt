package com.san1ch.vocabanana.feature.text.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.san1ch.vocabanana.core.essentials.model.constants.TextConstant
import com.san1ch.vocabanana.core.ui.compose.CollectResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AddTextScreen(
    viewModel: AddTextScreenViewModel = hiltViewModel(),
) {
    CollectResource(events = viewModel.events)

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let { selectedUri ->

            viewModel.onIntent(AddTextUiIntent.StartLoadingFile)

            scope.launch(Dispatchers.IO) {
                var name: String? = null
                context.contentResolver.query(selectedUri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex != -1) {
                        name = cursor.getString(nameIndex)
                    }
                }
                val finalName = name ?: "Selected file"

                val text = context.contentResolver.openInputStream(selectedUri)
                    ?.bufferedReader()
                    ?.use { it.readText() } ?: ""

                viewModel.onIntent(AddTextUiIntent.FileLoaded(finalName, text))
            }
        }
    }

    AddTextContent(
        state = state,
        onIntent = viewModel::onIntent,
        onOpenFileClick = { filePickerLauncher.launch("text/plain") },
        onPasteClick = {
            val pastedText = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
            if (!pastedText.isNullOrBlank()) {
                viewModel.onIntent(AddTextUiIntent.ContentChanged(pastedText))
            }
        },
        onCopyClick = {
            if (state.content.text.isNotBlank()) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("text", state.content.text))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTextContent(
    modifier: Modifier = Modifier,
    state: AddTextUiState,
    onIntent: (AddTextUiIntent) -> Unit,
    onOpenFileClick: () -> Unit,
    onPasteClick: () -> Unit,
    onCopyClick: () -> Unit,
) {
    val color by animateColorAsState(
        targetValue = if (state.isTitleTooLong) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
        label = stringResource(R.string.bordercolor),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_text_title)) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AddTextUiIntent.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = { onIntent(AddTextUiIntent.TitleChanged(it)) },
                label = {
                    Text(
                        stringResource(
                            R.string.title,
                            state.title.length,
                            TextConstant.MAX_NAME_LENGTH,
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.isTitleTooLong,
            )

            if (state.fileName != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(state.fileName, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            } else {
                OutlinedTextField(
                    value = state.content,
                    onValueChange = { onIntent(AddTextUiIntent.ContentChanged(it.text)) },
                    label = {
                        Text(
                            if (state.isLoadingFile) {
                                stringResource(R.string.loading_file)
                            } else {
                                stringResource(
                                    R.string.paste_your_text_here,
                                )
                            },
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    enabled = !state.isLoadingFile,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onCopyClick,
                    modifier = Modifier.weight(1f),
                    enabled = state.content.text.isNotBlank(),
                ) {
                    Text(stringResource(R.string.copy))
                }
                OutlinedButton(
                    onClick = onPasteClick,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoadingFile,
                ) {
                    Text(stringResource(R.string.paste))
                }
                OutlinedButton(
                    onClick = { onIntent(AddTextUiIntent.ClearClicked) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.clear))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onOpenFileClick,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoadingFile,
                ) {
                    Icon(Icons.Default.AttachFile, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.pick_txt))
                }
                Button(
                    onClick = { onIntent(AddTextUiIntent.AddTextClicked) },
                    modifier = Modifier.weight(1f),
                    enabled = state.content.text.isNotBlank() && !state.isTitleTooLong && !state.isLoadingFile,
                ) {
                    Text(stringResource(R.string.add))
                }
            }
        }
    }
}
