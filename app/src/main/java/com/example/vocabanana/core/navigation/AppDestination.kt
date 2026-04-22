package com.example.vocabanana.core.navigation


import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * Screens has been used in [AppDestination]
 */
private object AppScreens {
    const val INIT_SCREEN = "InitScreen"
    const val MAIN_SCREEN = "MainScreen"
    const val SETTINGS_SCREEN = "SettingsScreen"
    const val VOCABULARY_SCREEN = "VocabularyScreen"
    const val TEXT_LIST_SCREEN = "TextListScreen"
    const val TEXT_CREATE_SCREEN = "TextCreateScreen"
    const val DEBUG_SCREEN = "DebugScreen"
    const val NEW_WORD_LIST_SCREEN = "NewWordListScreen"
}

/**
 * Arguments has been used in [AppDestination] routes
 */
object AppDestinationArgs {

}

/**
 * Destinations has been used in the [AppActivity]
 */
sealed class AppDestination(val route: String) {
    object Init : AppDestination(AppScreens.INIT_SCREEN)
    object Main : AppDestination(AppScreens.MAIN_SCREEN)
    object Settings : AppDestination(AppScreens.SETTINGS_SCREEN)
    object Vocabulary : AppDestination(AppScreens.VOCABULARY_SCREEN)
    object TextList : AppDestination(AppScreens.TEXT_LIST_SCREEN)
    object AddText : AppDestination(AppScreens.TEXT_CREATE_SCREEN)
    object Debug : AppDestination(AppScreens.DEBUG_SCREEN)
    object NewWordList : AppDestination(AppScreens.NEW_WORD_LIST_SCREEN)

}

/**
 * It models the navigation actions in the app.
 */
class AppNavigationActions(private val navController: NavHostController) {
    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateTo(destination: AppDestination) {
        when (destination) {
            AppDestination.Debug -> navigateToDebug()

            AppDestination.Init -> navigateToInit()
            AppDestination.Main -> navigateToMain()
            AppDestination.Settings -> navigateToSettings()
            AppDestination.AddText -> navigateToCreateText()
            AppDestination.TextList -> navigateToTextList()
            AppDestination.Vocabulary -> navigateToVocabulary()
            AppDestination.NewWordList -> navigateToNewWordList()
        }
    }
    private fun navigateToDebug(){
        navController.navigate(AppDestination.Debug.route){

        }
    }



    private fun navigateToInit() {
        navController.navigate(AppDestination.Init.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }
    }
    private fun navigateToMain() {
        navController.navigate(AppDestination.Main.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    private fun navigateToSettings() {
        navController.navigate(AppDestination.Settings.route) {

        }
    }
    private fun navigateToVocabulary() {
        navController.navigate(AppDestination.Vocabulary.route) {

        }
    }
    private fun navigateToTextList() {
        navController.navigate(AppDestination.TextList.route) {

        }
    }
    private fun navigateToCreateText() {
        navController.navigate(AppDestination.AddText.route) {

        }
    }
    private fun navigateToNewWordList() {
        navController.navigate(AppDestination.NewWordList.route) {

        }
    }
}
