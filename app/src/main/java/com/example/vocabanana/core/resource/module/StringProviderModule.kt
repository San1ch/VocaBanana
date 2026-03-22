package com.example.vocabanana.core.resource.module

import com.example.vocabanana.core.resource.AndroidStringProvider
import com.example.vocabanana.core.resource.StringProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StringProviderModule {

    @Binds
    @Singleton
    abstract fun provideStringProvider(impl: AndroidStringProvider): StringProvider
}