package com.san1ch.vocabanana.navigation.routers

import com.san1ch.vocabanana.feature.init.presentation.InitRouter
import com.san1ch.vocabanana.navigation.AppRouter
import com.san1ch.vocabanana.navigation.MainRoute
import javax.inject.Inject

class InitRouterImpl
@Inject
constructor(
    private val appRouter: AppRouter,
) : InitRouter {
    override fun navigateToMain() {
        appRouter.resetTo(MainRoute)
    }
}
