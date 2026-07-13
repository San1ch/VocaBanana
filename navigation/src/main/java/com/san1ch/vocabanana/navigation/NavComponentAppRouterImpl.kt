package com.san1ch.vocabanana.navigation

import androidx.navigation.NavController
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavComponentAppRouterImpl
@Inject
constructor() : AppRouter {
    private val _navigationCommands = Channel<(NavController) -> Unit>(Channel.BUFFERED)
    val navigationCommands = _navigationCommands.receiveAsFlow()

    override fun navigateTo(route: Route) {
        _navigationCommands.trySend { navController ->
            navController.navigate(route)
        }
    }

    override fun resetTo(route: Route) {
        _navigationCommands.trySend { navController ->
            navController.navigate(route) {
                popUpTo(0)
            }
        }
    }

    override fun navigateBack() {
        _navigationCommands.trySend { navController ->
            navController.navigateUp()
        }
    }
}
