package com.san1ch.vocabanana.navigation.routers

import com.san1ch.vocabanana.feature.mainsettings.presentation.SettingsRouter
import com.san1ch.vocabanana.navigation.AppRouter
import javax.inject.Inject

class SettingsRouterImpl @Inject constructor(
    private val appRouter: AppRouter
) : SettingsRouter {
    override fun navigateBack() {
        appRouter.navigateBack()
    }
}