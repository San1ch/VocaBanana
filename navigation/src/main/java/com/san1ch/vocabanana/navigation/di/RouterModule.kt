package com.san1ch.vocabanana.navigation.di

import com.san1ch.vocabanana.feature.init.presentation.InitRouter
import com.san1ch.vocabanana.feature.main.presentation.MainRouter
import com.san1ch.vocabanana.feature.mainsettings.presentation.SettingsRouter
import com.san1ch.vocabanana.feature.text.presentation.AddTextRouter
import com.san1ch.vocabanana.feature.text.presentation.TextListRouter
import com.san1ch.vocabanana.feature.vocabulary.presentation.router.VocabularyRouter
import com.san1ch.vocabanana.navigation.InitRoute
import com.san1ch.vocabanana.navigation.MainRoute
import com.san1ch.vocabanana.navigation.routers.AddTextRouterImpl
import com.san1ch.vocabanana.navigation.routers.InitRouterImpl
import com.san1ch.vocabanana.navigation.routers.MainRouteImpl
import com.san1ch.vocabanana.navigation.routers.SettingsRouterImpl
import com.san1ch.vocabanana.navigation.routers.TextListRouterImpl
import com.san1ch.vocabanana.navigation.routers.VocabularyRouterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RouterModule {

    @Binds
    fun bindInitRoute(initRoute: InitRouterImpl): InitRouter

    @Binds
    fun bindMainRoute(mainRoute: MainRouteImpl): MainRouter

    @Binds
    fun bindAddTextRouter(addTextRouter: AddTextRouterImpl): AddTextRouter

    @Binds
    fun bindTextListRouter(textListRouter: TextListRouterImpl): TextListRouter

    @Binds
    fun bindSettingsRouter(settingsRouter: SettingsRouterImpl): SettingsRouter

    @Binds
    fun bindVocabularyRouter(vocabularyRouter: VocabularyRouterImpl): VocabularyRouter
}