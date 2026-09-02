package com.san1ch.vocabanana.feature.main.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.san1ch.vocabanana.core.ui.compose.AppConfirmDialogWithResult
import com.san1ch.vocabanana.core.ui.compose.CollectUiEvents
import com.san1ch.vocabanana.core.ui.theme.BackupColor

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = hiltViewModel(),
) {
    CollectUiEvents(events = viewModel.events)

    val backupPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let { viewModel.onIntent(MainUiIntent.LoadLocalBackup(it.toString())) }
    }

    val state by viewModel.uiState.collectAsState()

    MainContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBackupPicker = { backupPicker.launch(arrayOf("*/*")) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    state: MainUiState,
    onIntent: (MainUiIntent) -> Unit,
    onBackupPicker: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(state.appName, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                actions = {
                    MainTopBarActions(
                        state = state,
                        onIntent = onIntent,
                        onBackupPicker = onBackupPicker,
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    MenuCard(
                        title = stringResource(R.string.vocabulary),
                        icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                        accentColor = MaterialTheme.colorScheme.primary,
                        onClick = { onIntent(MainUiIntent.NavigateToVocabulary) },
                    )
                }
                item {
                    MenuCard(
                        title = stringResource(R.string.texts),
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        accentColor = MaterialTheme.colorScheme.secondary,
                        onClick = { onIntent(MainUiIntent.NavigateToTexts) },
                    )
                }
            }
        }
    }

    MainBackupDialogs(state = state, onIntent = onIntent)
}

@Composable
private fun MainBackupDialogs(
    state: MainUiState,
    onIntent: (MainUiIntent) -> Unit,
) {
    AppConfirmDialogWithResult(
        show = state.confirmLocalBackupWindowState,
        title = "Save backup",
        startText = "Do you want to save backup?",
        onConfirm = { onIntent(MainUiIntent.LocalBackup) },
        onDismiss = { onIntent(MainUiIntent.CloseConfirmLocalBackupWindow) },
        result = state.localBackupResult,
        loadingTest = "Saving backup...",
    )

    AppConfirmDialogWithResult(
        show = state.confirmCloudBackupWindowState,
        title = "Save Cloud Backup",
        startText = "Do you want to save backup to the cloud?",
        onConfirm = { onIntent(MainUiIntent.BackupCloud) },
        onDismiss = { onIntent(MainUiIntent.CloseConfirmCloudBackupWindow) },
        result = state.cloudBackupResult,
        loadingTest = "Saving backup to the cloud...",
    )

    AppConfirmDialogWithResult(
        show = state.confirmLoadCloudBackupWindowState,
        title = "Load Cloud Backup",
        startText = "Do you want to restore backup from the cloud? Current local data may be overwritten.",
        onConfirm = { onIntent(MainUiIntent.LoadCloudBackup) },
        onDismiss = { onIntent(MainUiIntent.CloseConfirmLoadCloudBackupWindow) },
        result = state.loadCloudBackupResult,
        loadingTest = "Loading backup from the cloud...",
    )
}

@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val containerColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.surface
    } else {
        Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(width = 1.dp, color = accentColor.copy(alpha = 0.2f)),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.4f)
                    .height(4.dp)
                    .padding(bottom = 8.dp),
                shape = CircleShape,
                color = accentColor,
            ) {}

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = accentColor.copy(alpha = 0.1f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = accentColor,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun MainTopBarActions(
    state: MainUiState,
    onIntent: (MainUiIntent) -> Unit,
    onBackupPicker: () -> Unit,
) {
    if (BuildConfig.DEBUG) {
        IconButton(onClick = { onIntent(MainUiIntent.NavigateToDebug) }) {
            Icon(Icons.Default.BugReport, contentDescription = "Debug")
        }
    }

    if (state.isLocalBackupEnabled || state.isCloudBackupEnabled) {
        var isBackupMenuExpanded by remember { mutableStateOf(false) }

        val backupTint =
            if (state.isLocalBackupNeedUpdate || state.isCloudBackupNeedUpdate) BackupColor.NeedUpdate else LocalContentColor.current

        val localBackupTint =
            if (state.isLocalBackupNeedUpdate) BackupColor.NeedUpdate else LocalContentColor.current
        val cloudBackupTint =
            if (state.isCloudBackupNeedUpdate) BackupColor.NeedUpdate else LocalContentColor.current

        Box {
            IconButton(onClick = { isBackupMenuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = "Backup Options",
                    tint = backupTint,
                )
            }

            DropdownMenu(
                expanded = isBackupMenuExpanded,
                onDismissRequest = { isBackupMenuExpanded = false },
            ) {
                if (state.isLocalBackupEnabled) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.save_local_backup)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = "Save Local Backup",
                                tint = localBackupTint,
                            )
                        },
                        onClick = {
                            isBackupMenuExpanded = false
                            if (state.isLocalBackupNeedUpdate) {
                                onIntent(MainUiIntent.OpenConfirmLocalBackupWindow)
                            } else {
                                onIntent(MainUiIntent.ShowLastBackupTime)
                            }
                        },
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.load_local_backup)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = "Load Local Backup",
                                tint = LocalContentColor.current,
                            )
                        },
                        onClick = {
                            isBackupMenuExpanded = false
                            onBackupPicker()
                        },
                    )
                }

                if (state.isCloudBackupEnabled) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.save_cloud_backup)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = "Save Cloud Backup",
                                tint = cloudBackupTint,
                            )
                        },
                        onClick = {
                            isBackupMenuExpanded = false
                            if (state.isCloudBackupNeedUpdate) {
                                onIntent(MainUiIntent.OpenConfirmCloudBackupWindow)
                            } else {
                                onIntent(MainUiIntent.ShowLastCloudBackupTime)
                            }
                        },
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.load_cloud_backup)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = "Load Cloud Backup",
                                tint = LocalContentColor.current,
                            )
                        },
                        onClick = {
                            isBackupMenuExpanded = false
                            onIntent(MainUiIntent.OpenConfirmLoadCloudBackupWindow)
                        },
                    )
                }
            }
        }
    }

    IconButton(onClick = { onIntent(MainUiIntent.NavigateToSettings) }) {
        Icon(Icons.Default.Settings, contentDescription = "Settings")
    }
}
