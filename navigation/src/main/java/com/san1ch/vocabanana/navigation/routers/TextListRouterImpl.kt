package com.san1ch.vocabanana.navigation.routers

import com.san1ch.vocabanana.feature.text.presentation.TextListRouter
import com.san1ch.vocabanana.navigation.AddTextRoute
import com.san1ch.vocabanana.navigation.AppRouter
import javax.inject.Inject

class TextListRouterImpl
@Inject
constructor(
    private val appRouter: AppRouter,
) : TextListRouter {
    override fun navigateToAddText() {
        appRouter.navigateTo(AddTextRoute)
    }
}
