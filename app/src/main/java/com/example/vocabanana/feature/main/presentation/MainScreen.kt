package com.example.vocabanana.feature.main.presentation

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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.BuildConfig
import com.example.vocabanana.core.navigation.AppDestination
import com.example.vocabanana.ui.composable.CollectUiEvents
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
        onDebugClick = { navigateTo(AppDestination.Debug) },
        onSettingsClick = { navigateTo(AppDestination.Settings) },
        onVocabClick = { navigateTo(AppDestination.Vocabulary) },
        onTextsClick = { navigateTo(AppDestination.TextList) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    onDebugClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onVocabClick: () -> Unit,
    onTextsClick: () -> Unit,
) {
    Scaffold(
        // The Scaffold now uses the slightly darker background color
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("VocaBanana", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface, // TopBar stays White
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    if (BuildConfig.DEBUG) {
                        IconButton(onClick = onDebugClick) {
                            Icon(Icons.Default.BugReport, contentDescription = "Debug")
                        }
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    MenuCard(
                        title = "Vocabulary",
                        icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                        accentColor = MaterialTheme.colorScheme.primary,
                        onClick = onVocabClick
                    )
                }
                item {
                    MenuCard(
                        title = "Texts",
                        icon = Icons.Default.MenuBook,
                        accentColor = MaterialTheme.colorScheme.secondary,
                        onClick = onTextsClick
                    )
                }
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp) // Slightly taller feels more "premium"
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large, // Rounder corners for menu
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme())
                MaterialTheme.colorScheme.surface
            else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            width = 1.dp,
            color = accentColor.copy(alpha = 0.2f) // Tinted border
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. The Accent Element: A decorative "pill" at the bottom
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.4f) // Only 40% width
                    .height(4.dp)
                    .padding(bottom = 8.dp),
                shape = CircleShape,
                color = accentColor
            ) {}

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 2. The Icon: Put it in a soft "ghost" background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = accentColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = accentColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold, // Stronger weight
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    VocabBananaTheme(darkTheme = false) {
        MainContent({}, {}, {}, {})
    }
}