package com.example.vocabanana.core.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.feature.debug.presentation.DebugScreen
import com.example.vocabanana.feature.init.InitScreen
import com.example.vocabanana.feature.main.presentation.MainScreen
import com.example.vocabanana.feature.settings.presentation.SettingsScreen
import com.example.vocabanana.feature.text.presentation.AddTextScreen
import com.example.vocabanana.feature.text.presentation.textlistscreenpages.TextListScreen
import com.example.vocabanana.feature.vocabulary.presentation.NewWordListScreen
import com.example.vocabanana.feature.vocabulary.presentation.vocabularyscreenpages.VocabularyScreen


@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    appNavigationActions: AppNavigationActions = remember(navController) {
        AppNavigationActions(navController)
    },
    viewModel: NavGraphViewModel = hiltViewModel()
) {
    val state by viewModel.startDestination.collectAsState()
    StateObserver(state = state) { isInit ->

        val destination = if (isInit) AppDestination.Init.route else AppDestination.Main.route
        NavHost(
            navController = navController, startDestination = destination, modifier = modifier
        ) {
            // Debug version screens
            composable(AppDestination.Debug.route) {
                DebugScreen(
                    navigateBack = appNavigationActions::navigateBack,
                    navigateTo = { appNavigationActions.navigateTo(it) })
            }

            // Release version screens
            composable(AppDestination.Main.route) {
                MainScreen(
                    navigateTo = { appNavigationActions.navigateTo(it) })
            }
            composable(AppDestination.Init.route) {
                InitScreen(
                    navigateTo = { appNavigationActions.navigateTo(it) })
            }
            composable(AppDestination.Settings.route) {
                SettingsScreen(
                    navigateTo = { appNavigationActions.navigateTo(it) },
                    navigateBack = appNavigationActions::navigateBack
                )
            }
            composable(AppDestination.Vocabulary.route) {
                VocabularyScreen(
                    navigateTo = { appNavigationActions.navigateTo(it) },
                    navigateBack = appNavigationActions::navigateBack
                )
            }
            composable(AppDestination.TextList.route) {
                TextListScreen(
                    navigateTo = { appNavigationActions.navigateTo(it) },
                    navigateBack = appNavigationActions::navigateBack
                )
            }
            composable(AppDestination.AddText.route) {
                AddTextScreen(
                    navigateTo = { appNavigationActions.navigateTo(it) },
                    navigateBack = appNavigationActions::navigateBack
                )
            }
            composable(AppDestination.NewWordList.route) {
                NewWordListScreen(
                    navigateTo = { appNavigationActions.navigateTo(it) },
                    navigateBack = appNavigationActions::navigateBack
                )
            }
        }
    }
}
