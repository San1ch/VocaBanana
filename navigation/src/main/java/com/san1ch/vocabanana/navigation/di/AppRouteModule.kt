package com.san1ch.vocabanana.navigation.di

import com.san1ch.vocabanana.navigation.AppRouter
import com.san1ch.vocabanana.navigation.NavComponentAppRouterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppRouteModule {
    @Binds
    fun bindAppRouter(appRouter: NavComponentAppRouterImpl): AppRouter
}
