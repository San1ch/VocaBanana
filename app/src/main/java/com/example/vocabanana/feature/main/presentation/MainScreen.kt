package com.example.vocabanana.feature.main.presentation

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        topBar = {
            TopAppBar(
                title = { Text("VocaBanana", fontWeight = FontWeight.Bold) },
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
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MenuCard(
                        title = "Vocabulary",
                        icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                        onClick = onVocabClick
                    )
                }
                item {
                    MenuCard(
                        title = "Texts",
                        icon = Icons.Default.MenuBook,
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
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        // This line removes the "blue/grey" tint by forcing the surface color
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circle background for the icon to make it pop
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
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