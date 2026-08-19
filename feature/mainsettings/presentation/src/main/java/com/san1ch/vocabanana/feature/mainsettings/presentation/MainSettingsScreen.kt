package com.san1ch.vocabanana.feature.mainsettings.presentation

import android.app.Activity
import android.app.PendingIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.san1ch.vocabanana.core.essentials.model.AppThemeMode
import com.san1ch.vocabanana.core.ui.compose.CollectUiEvents

@Composable
fun MainSettingsScreen(
    viewModel: MainSettingsScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CollectUiEvents(
        events = viewModel.events,
    )

    val launchGoogleAuth = rememberGoogleAuthHandler(
        onSuccess = {
            viewModel.onIntent(SettingsIntent.GoogleAuthResolved)
        },
        onError = { code, message ->
            Log.e("MainSettingsScreen", "Auth error code: $code, msg: $message")
        },
    )

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SettingsUiEvent.LaunchGoogleConsent -> {
                    launchGoogleAuth(event.pendingIntent)
                }
            }
        }
    }
    AppSettingsContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun rememberGoogleAuthHandler(
    onSuccess: () -> Unit,
    onError: (Int, String?) -> Unit = { _, _ -> },
): (PendingIntent) -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                Log.d("GoogleAuthHandler", "Authorization successful!")
                onSuccess()
            }

            Activity.RESULT_CANCELED -> {
                val data = result.data
                val extras = data?.extras
                val errorMsg =
                    extras?.keySet()?.joinToString(", ") { key -> "$key=${extras.get(key)}" }
                        ?: "No extras"
                onError(Activity.RESULT_CANCELED, "User canceled or flow interrupted: $errorMsg")
            }

            else -> {
                onError(result.resultCode, "Unknown result code: ${result.resultCode}")
            }
        }
    }

    return remember {
        { pendingIntent ->
            try {
                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
                launcher.launch(intentSenderRequest)
            } catch (e: Exception) {
                Log.e("GoogleAuthHandler", "Failed to launch intent sender", e)
                onError(-99, e.localizedMessage ?: "Launch exception")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsContent(
    state: SettingsUiState,
    onIntent: (SettingsIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(SettingsIntent.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            SettingsSectionTitle(title = stringResource(R.string.appearance))

            SettingsDropdownItem(
                label = stringResource(R.string.app_theme),
                currentValue = state.currentTheme.label,
                options = AppThemeMode.entries.map { it.label },
                onOptionSelected = { label ->
                    val theme = AppThemeMode.entries.find { it.label == label } ?: AppThemeMode.AUTO
                    onIntent(SettingsIntent.ChangeTheme(theme))
                },
            )

            SettingsSectionTitle(title = "Data & Backup")

            BackupSections(
                state = state,
                onIntent = onIntent,
            )
        }
    }
}

@Composable
private fun BackupSections(
    state: SettingsUiState,
    onIntent: (SettingsIntent) -> Unit,
) {
    val context = LocalContext.current

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
    ) { uri ->
        uri?.let { selectedUri ->
            try {
                val flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(selectedUri, flags)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            onIntent(SettingsIntent.UpdateLocalPathUri(selectedUri))
        }
    }

    SettingsSwitchItem(
        label = "Local Backup",
        checked = state.isLocalToggleEnabled,
        onCheckedChange = { onIntent(SettingsIntent.ToggleLocalBackup(it)) },
    )

    AnimatedVisibility(
        visible = state.isLocalToggleEnabled,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            val pathText = state.selectedLocalPath.ifEmpty { "Not selected" }
            Text(
                text = "Path: $pathText",
                style = MaterialTheme.typography.bodyMedium,
                color = if (state.selectedLocalPath.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error,
            )
            Button(
                onClick = { folderPickerLauncher.launch(null) },
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Choose Backup Folder")
            }

            if (state.selectedLocalPath.isEmpty()) {
                Text(
                    text = "Please choose a folder to continue",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }

    SettingsSwitchItem(
        label = "Cloud Backup",
        checked = state.isCloudToggleEnabled,
        onCheckedChange = { onIntent(SettingsIntent.ToggleCloudBackup(it)) },
    )

    AnimatedVisibility(
        visible = state.isCloudToggleEnabled,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

            if(state.isCloudBackupEnabled && state.currentUserEmail != null){
                Text(
                    text = buildAnnotatedString {
                        append("Connected to: ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(state.currentUserEmail)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
            } else{
                Text(
                    text = "Connect your Google account to securely store and sync your vocabulary data in your private Google Drive app folder.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            // First is "text", second is "onClick"
            val button: Pair<String, () -> Unit> = when {
                state.isCloudBackupEnabled -> Pair("Disconnect") { onIntent(SettingsIntent.DisconnectGoogleDriveClicked) }

                else -> Pair("Sign in with Google") { onIntent(SettingsIntent.ConnectGoogleDriveClicked) }
            }
            Button(
                onClick = button.second,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(button.first)
            }
        }
    }

    AnimatedVisibility(
        visible = state.hasUnsavedChanges,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column {
            Button(
                onClick = { onIntent(SettingsIntent.SaveBackupSettings) },
                enabled = state.isSaveValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text("Save Backup Settings")
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
private fun SettingsClickableItem(
    label: String,
    onClick: (() -> Unit)? = null,
    control: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
        )
        control?.invoke()
    }
}

@Composable
private fun SettingsSwitchItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SettingsClickableItem(
        label = label,
        onClick = { onCheckedChange(!checked) },
        control = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        },
    )
}

@Composable
private fun SettingsDropdownItem(
    label: String,
    currentValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    SettingsClickableItem(
        label = label,
        onClick = { expanded = true },
        control = {
            Box(contentAlignment = Alignment.TopEnd) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(currentValue, color = MaterialTheme.colorScheme.outline)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onOptionSelected(option)
                                expanded = false
                            },
                        )
                    }
                }
            }
        },
    )
}
