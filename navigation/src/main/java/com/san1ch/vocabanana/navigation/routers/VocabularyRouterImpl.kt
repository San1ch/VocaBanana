package com.san1ch.vocabanana.navigation.routers

import com.san1ch.vocabanana.feature.vocabulary.presentation.router.VocabularyRouter
import com.san1ch.vocabanana.navigation.AppRouter
import com.san1ch.vocabanana.navigation.NewWordListRoute
import javax.inject.Inject

class VocabularyRouterImpl @Inject constructor(
    private val appRouter: AppRouter
) : VocabularyRouter {
    override fun navigateBack() {
        appRouter.navigateBack()
    }

    override fun navigateToNewWords() {
        appRouter.navigateTo(NewWordListRoute)
    }
}