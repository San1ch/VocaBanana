package com.example.vocabanana.core.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vocabanana.core.navigation.composable.StateContainer
import com.example.vocabanana.features.init.InitScreen
import com.example.vocabanana.features.main.MainScreen
import com.example.vocabanana.features.vocabulary.presentation.VocabularyScreen
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
    val startDestinationState by viewModel.startDestination.collectAsStateWithLifecycle()
    
    StateContainer(state = startDestinationState) { destination ->
        NavHost(
            navController = navController,
            startDestination = destination,
            modifier = modifier
        ) {
            composable(AppDestinations.MAIN_DESTINATION) {
                MainScreen(
                    onVocabClick = { appNavigationActions.navigateToVocabulary() }
                )
            }
            composable(AppDestinations.INIT_DESTINATION) {
                InitScreen(
                    onFinished = { appNavigationActions.navigateToMain() }
                )
            }
            composable(AppDestinations.VOCABULARY_DESTINATION) {
                VocabularyScreen()
            }
        }
    }
}