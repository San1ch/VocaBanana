package com.san1ch.vocabanana.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.san1ch.vocabanana.core.essentials.database.model.AppThemeMode
import com.san1ch.vocabanana.core.essentials.database.repositories.SettingsRepository
import com.san1ch.vocabanana.core.ui.theme.VocaBananaTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppNavHostViewModel @Inject constructor(
    val router: NavComponentAppRouterImpl,
    private val repository: SettingsRepository // Injecting repository to read global theme
) : ViewModel() {

    // Expose the global theme to the UI layer
    val themeState: StateFlow<AppThemeMode> = repository.themeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppThemeMode.AUTO
        )
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navGraphBuilder: NavGraphBuilder.() -> Unit = {},
    startDestination: Route = MainRoute,
    viewModel: AppNavHostViewModel = hiltViewModel()
) {
    val router = viewModel.router

    // Collecting the global theme state
    val themeMode by viewModel.themeState.collectAsStateWithLifecycle()

    // Determine whether to use dark theme based on system or user selection
    val useDarkTheme = when (themeMode) {
        AppThemeMode.AUTO -> isSystemInDarkTheme()
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    // Passing the dynamically calculated value to your design system theme
    VocaBananaTheme(darkTheme = useDarkTheme) {
        val navController = rememberNavController()

        LaunchedEffect(navController) {
            router.navigationCommands.collect { command ->
                command(navController)
            }
        }

        val navGraph = remember {
            navController.createGraph(startDestination) {
                buildAppNavGraph()
                navGraphBuilder()
            }
        }

        NavHost(
            modifier = modifier.fillMaxSize(),
            navController = navController,
            graph = navGraph
        )
    }
}