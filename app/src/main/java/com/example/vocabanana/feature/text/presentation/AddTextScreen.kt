package com.example.vocabanana.feature.text.presentation

import android.content.ClipData
import android.content.ClipboardManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.R
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.feature.text.domain.model.TextConstant
import com.example.vocabanana.ui.composable.CollectUiEvents

@Composable
fun AddTextScreen(
    viewModel: AddTextScreenViewModel = hiltViewModel(),
    navigateTo: (AppDestination) -> Unit,
    navigateBack: () -> Unit,
) {
    CollectUiEvents(
        events = viewModel.events,
        navigateBack = navigateBack,
        navigateTo = { navigateTo(it) }
    )

    val context = LocalContext.current
    val clipboardManager = LocalContext.current.getSystemService(
        ClipboardManager::class.java
    )

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val onCopyClick = {
        val clip = ClipData.newPlainText("text", content)
        clipboardManager.setPrimaryClip(clip)
    }

    val onPasteClick = {
        val pasted = clipboardManager.primaryClip
            ?.getItemAt(0)
            ?.text
            ?.toString()

        if (!pasted.isNullOrBlank()) {
            content = pasted
        }
    }

    val onClearClick = {
        content = ""
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val text = context.contentResolver.openInputStream(it)?.bufferedReader()
                ?.use { reader -> reader.readText() } ?: ""
            content = text
        }
    }

    AddTextContent(
        title = title,
        onTitleChange = { title = it },
        content = content,
        onContentChange = { content = it },
        onBackClick = navigateBack,
        onOpenFileClick = { filePickerLauncher.launch("text/plain") },
        onAddTextClick = { viewModel.addText(title, content) },
        onCopyClick = onCopyClick,
        onPasteClick = onPasteClick,
        onClearClick = onClearClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTextContent(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    onBackClick: () -> Unit,

    onOpenFileClick: () -> Unit,
    onAddTextClick: () -> Unit,

    onCopyClick: () -> Unit,
    onPasteClick: () -> Unit,
    onClearClick: () -> Unit

) {
    val isTooLong = title.length > TextConstant.MAX_NAME_LENGTH
    val color by animateColorAsState(
        targetValue = if (isTooLong) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.secondary,
        label = "borderColor"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isTooLong) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.secondary,
        label = "labelColor"
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.add_text_title)) }, navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = {
                    Text(
                        text = "Title: ${title.length}/${TextConstant.MAX_NAME_LENGTH} chars",
                        color = labelColor
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = color,
                    unfocusedBorderColor = color,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    cursorColor = color
                )
            )

            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                label = { Text("Paste your text here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 5
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Copy")
                }

                OutlinedButton(
                    onClick = onPasteClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Paste")
                }

                OutlinedButton(
                    onClick = onClearClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenFileClick, modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Pick .txt")
                }

                Button(
                    onClick = onAddTextClick,
                    modifier = Modifier.weight(1f),
                    enabled = content.isNotBlank()
                ) {
                    Text("Add")
                }
            }
        }
    }
}