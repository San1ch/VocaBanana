package com.example.vocabanana.core.navigation


import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * Screens used in [AppDestinations]
 */
private object AppScreens {
    const val INIT_SCREEN = "InitScreen"
    const val MAIN_SCREEN = "MainScreen"
    const val VOCABULARY_SCREEN = "VocabularyScreen"
    const val TEXT_LIST_SCREEN = "TextListScreen"
}

/**
 * Arguments used in [AppDestinations] routes
 */
object AppDestinationsArgs {
}

/**
 * Destinations used in the [AppActivity]
 */
object AppDestinations {
    const val INIT_DESTINATION = AppScreens.INIT_SCREEN
    const val MAIN_DESTINATION = AppScreens.MAIN_SCREEN
    const val VOCABULARY_DESTINATION = AppScreens.VOCABULARY_SCREEN
    const val TEXT_LIST_DESTINATION = AppScreens.TEXT_LIST_SCREEN
}

/**
 * Models the navigation actions in the app.
 */
class AppNavigationActions(private val navController: NavHostController) {


    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateToInit() {
        navController.navigate(AppDestinations.INIT_DESTINATION) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    fun navigateToMain() {
        navController.navigate(AppDestinations.MAIN_DESTINATION) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToVocabulary() {
        navController.navigate(AppDestinations.VOCABULARY_DESTINATION) {

        }
    }

    fun navigateToTexts() {
        navController.navigate(AppDestinations.TEXT_LIST_DESTINATION) {

        }
    }



}
