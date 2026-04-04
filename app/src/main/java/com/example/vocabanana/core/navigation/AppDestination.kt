package com.example.vocabanana.core.navigation


import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * Screens used in [AppDestination]
 */
private object AppScreens {
    const val INIT_SCREEN = "InitScreen"
    const val MAIN_SCREEN = "MainScreen"
    const val VOCABULARY_SCREEN = "VocabularyScreen"
    const val TEXT_LIST_SCREEN = "TextListScreen"
    const val TEXT_CREATE_SCREEN = "TextCreateScreen"
    const val WORD_ANALYSIS_SCREEN = "WordAnalysisScreen"
}

/**
 * Arguments used in [AppDestination] routes
 */
object AppDestinationsArgs {

}

/**
 * Destinations used in the [AppActivity]
 */
sealed class AppDestination(val route: String) {
    object Init : AppDestination(AppScreens.INIT_SCREEN)
    object Main : AppDestination(AppScreens.MAIN_SCREEN)
    object Vocabulary : AppDestination(AppScreens.VOCABULARY_SCREEN)
    object TextList : AppDestination(AppScreens.TEXT_LIST_SCREEN)
    object TextCreate : AppDestination(AppScreens.TEXT_CREATE_SCREEN)
    object WordAnalysis : AppDestination(AppScreens.WORD_ANALYSIS_SCREEN)

}

/**
 * Models the navigation actions in the app.
 */
class AppNavigationActions(private val navController: NavHostController) {


    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateTo(destination: AppDestination) {
        when (destination) {
            AppDestination.Init -> navigateToInit()
            AppDestination.Main -> navigateToMain()
            AppDestination.TextCreate -> navigateToCreateText()
            AppDestination.TextList -> navigateToTextList()
            AppDestination.Vocabulary -> navigateToVocabulary()
            AppDestination.WordAnalysis -> navigateToWordAnalysis()
        }
    }

    //Navigate implementations
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

    private fun navigateToVocabulary() {
        navController.navigate(AppDestination.Vocabulary.route) {

        }
    }

    private fun navigateToTextList() {
        navController.navigate(AppDestination.TextList.route) {

        }
    }

    private fun navigateToCreateText() {
        navController.navigate(AppDestination.TextCreate.route) {

        }
    }
    private fun navigateToWordAnalysis() {
        navController.navigate(AppDestination.WordAnalysis.route) {

        }
    }


}
