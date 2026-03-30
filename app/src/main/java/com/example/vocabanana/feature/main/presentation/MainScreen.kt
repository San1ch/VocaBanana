package com.example.vocabanana.feature.main.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.BuildConfig
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.ui.composable.CollectUiEvents
import com.example.vocabanana.ui.composable.SpacerSmall
import com.example.vocabanana.ui.theme.VocabBananaTheme

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = hiltViewModel(),
    navigateTo: (AppDestination) -> Unit,
) {
    CollectUiEvents(
        events = viewModel.events,
        navigateBack = { },
        navigateTo = { navigateTo(it) }
    )
    MainContent(
        onDebugClick = { viewModel.reloadInit() },
        onMenuClick = { },
        onSettingsClick = { },
        onMoreClick = { },
        onVocabClick = { navigateTo(AppDestination.Vocabulary) },
        onTextsClick = { navigateTo(AppDestination.TextList) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    onDebugClick: () -> Unit,
    onMoreClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMenuClick: () -> Unit,
    onVocabClick: () -> Unit,
    onTextsClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vocab Banana") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    if (BuildConfig.DEBUG) {
                        IconButton(onClick = onDebugClick) {
                            Icon(Icons.Default.BugReport, contentDescription = "Debug")
                        }
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = onMoreClick) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },

                )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column() {
                Button(onClick = onVocabClick) {
                    Text("Vocabulary")
                }
                SpacerSmall()
                Button(onClick = onTextsClick) {
                    Text("Texts")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    VocabBananaTheme(darkTheme = false) {
        MainContent(
            onDebugClick = { },
            onMenuClick = { },
            onSettingsClick = { },
            onMoreClick = { },
            onVocabClick = { },
            onTextsClick = { }
        )
    }
}