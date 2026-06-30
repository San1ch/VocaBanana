package com.san1ch.vocabanana.navigation.routers

import com.san1ch.vocabanana.feature.text.presentation.AddTextRouter
import com.san1ch.vocabanana.navigation.AppRouter
import javax.inject.Inject

class AddTextRouterImpl @Inject constructor(
    private val appRouter: AppRouter
): AddTextRouter {
    override fun navigateBack() {
        appRouter.navigateBack()
    }
}