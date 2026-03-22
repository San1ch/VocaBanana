package com.example.vocabanana.features.vocabulary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.BuildConfig
import com.example.vocabanana.core.navigation.composable.StateContainer
import com.example.vocabanana.ui.theme.VocabBananaTheme

@Composable
fun VocabularyScreen(viewModel: VocabularyScreenViewModel = hiltViewModel()) {
    VocabularyContent(
        onMenuClick = {

        },
        onDebugClick = {

        },
        onMoreClick = {

        },
        onFilterListClick = {

        },
        onFilterSelected = { filterTag ->

        },
        onSearchChange = { searchValue ->

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyContent(
    onMenuClick: () -> Unit,
    onDebugClick: () -> Unit,
    onMoreClick: () -> Unit,
    onFilterListClick: () -> Unit,
    onFilterSelected: (String) -> Unit,
    onSearchChange: (String) -> Unit
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
                    IconButton(onClick = onFilterListClick) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
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
            //SearchBar
            OutlinedTextField(
                value = "",
                onValueChange = onSearchChange
            )
            LazyColumn() {
                items(100) { //TODO
                    Text("Item $it")
                }
            }
        }
    }
}

@Preview
@Composable
fun VocabularyScreenPreview() {
    VocabBananaTheme(darkTheme = false) {
        VocabularyContent(
            onMenuClick = { },
            onDebugClick = { },
            onMoreClick = { },
            onFilterListClick = { },
            onFilterSelected = { },
            onSearchChange = { }
        )
    }
}
