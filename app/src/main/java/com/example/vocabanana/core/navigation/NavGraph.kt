package com.example.vocabanana.core.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vocabanana.core.presentation.StateObserver
import com.example.vocabanana.feature.init.InitScreen
import com.example.vocabanana.feature.main.presentation.MainScreen
import com.example.vocabanana.feature.text.presentation.AddTextScreen
import com.example.vocabanana.feature.text.presentation.TextListScreen
import com.example.vocabanana.feature.vocabulary.presentation.VocabularyScreen
import com.example.vocabanana.feature.wordanalysis.presentation.WordAnalysisScreen
import kotlinx.coroutines.CoroutineScope


@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
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
            composable(AppDestination.Main.route) {
                MainScreen(
                    navigateTo = { appNavigationActions.navigateTo(it) })
            }
            composable(AppDestination.Init.route) {
                InitScreen(
                    navigateTo = { appNavigationActions.navigateTo(it) })
            }
            composable(AppDestination.Vocabulary.route) {
                VocabularyScreen()
            }
            composable(AppDestination.TextList.route) {
                TextListScreen(
                    navigateTo = { appNavigationActions.navigateTo(it) },
                    navigateBack = appNavigationActions::navigateBack
                )
            }
            composable(AppDestination.TextCreate.route) {
                AddTextScreen(
                    navigateTo = { appNavigationActions.navigateTo(it) },
                    navigateBack = appNavigationActions::navigateBack
                )
            }
            composable(AppDestination.WordAnalysis.route){
                WordAnalysisScreen()
            }
        }
    }
}