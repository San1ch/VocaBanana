package com.example.vocabanana.feature.database.text.module

import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.feature.database.text.repository.TextRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TextRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTextRepository(impl: TextRepositoryImpl): TextRepository

}