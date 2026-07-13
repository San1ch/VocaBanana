package com.san1ch.vocabanana.navigation.routers

import com.san1ch.vocabanana.feature.main.presentation.MainRouter
import com.san1ch.vocabanana.navigation.AppRouter
import com.san1ch.vocabanana.navigation.DebugRoute
import com.san1ch.vocabanana.navigation.MainSettingsRoute
import com.san1ch.vocabanana.navigation.TextListRoute
import com.san1ch.vocabanana.navigation.VocabularyRoute
import javax.inject.Inject

class MainRouteImpl
@Inject
constructor(
    private val appRouter: AppRouter,
) : MainRouter {
    override fun navigateToMainSettings() {
        appRouter.navigateTo(MainSettingsRoute)
    }

    override fun navigateToVocabulary() {
        appRouter.navigateTo(VocabularyRoute)
    }

    override fun navigateToTextList() {
        appRouter.navigateTo(TextListRoute)
    }

    override fun navigateToDebug() {
        appRouter.navigateTo(DebugRoute)
    }
}
