package com.san1ch.vocabanana

import com.san1ch.vocabanana.core.essentials.resources.AppStringProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    @Binds
    fun bindAppStringProvider(impl: AppStringProviderImpl): AppStringProvider
}