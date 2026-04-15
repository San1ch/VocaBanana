package com.example.vocabanana.feature.text.presentation

import android.content.ClipData
import android.content.ClipboardManager
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.R
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.feature.text.domain.model.TextConstant
import com.example.vocabanana.ui.composable.CollectUiEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val scope = rememberCoroutineScope()
    val clipboardManager = context.getSystemService(ClipboardManager::class.java)

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            fileName = selectedUri.lastPathSegment ?: "Selected file"

            scope.launch(Dispatchers.IO) {
                val text = context.contentResolver.openInputStream(selectedUri)
                    ?.bufferedReader()
                    ?.use { it.readText() } ?: ""

                withContext(Dispatchers.Main) {
                    content = text
                }
            }
        }
    }

    AddTextContent(
        title = title,
        onTitleChange = { title = it },
        content = content,
        onContentChange = {
            content = it
            if (it.isNotBlank()) fileName = null
        },
        fileName = fileName,
        onBackClick = navigateBack,
        onOpenFileClick = { filePickerLauncher.launch("text/plain") },
        onAddTextClick = { viewModel.addText(title, content) },
        onCopyClick = {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("text", content))
        },
        onPasteClick = {
            val pasted = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
            if (!pasted.isNullOrBlank()) {
                content = pasted
                fileName = null
            }
        },
        onClearClick = {
            content = ""
            fileName = null
        }
    )
}

// TODO
//  1. fix "document:100xxxxxxxxx" at the


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTextContent(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    fileName: String?,
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
        targetValue = if (isTooLong) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
        label = "borderColor"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_text_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
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
                label = { Text("Title: ${title.length}/${TextConstant.MAX_NAME_LENGTH}") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isTooLong
            )

            if (fileName != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(fileName, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            } else {
                OutlinedTextField(
                    value = content,
                    onValueChange = onContentChange,
                    label = { Text("Paste your text here") },
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onCopyClick, modifier = Modifier.weight(1f)) { Text("Copy") }
                OutlinedButton(onClick = onPasteClick, modifier = Modifier.weight(1f)) { Text("Paste") }
                OutlinedButton(onClick = onClearClick, modifier = Modifier.weight(1f)) { Text("Clear") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenFileClick, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.AttachFile, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Pick .txt")
                }
                Button(
                    onClick = onAddTextClick,
                    modifier = Modifier.weight(1f),
                    enabled = content.isNotBlank() && !isTooLong
                ) {
                    Text("Add")
                }
            }
        }
    }
}