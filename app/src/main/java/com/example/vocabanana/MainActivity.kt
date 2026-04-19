package com.example.vocabanana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vocabanana.core.navigation.NavGraph
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.core.presentation.settings.AppTheme
import com.example.vocabanana.ui.theme.VocabBananaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val themeState by viewModel.currentTheme.collectAsState()

            StateObserver(state = themeState) { state ->
                VocabBananaTheme(
                    darkTheme = state == AppTheme.DARK,
                    dynamicColor = state == AppTheme.AUTO
                ) {
                    NavGraph()
                }
            }
        }
    }
}
