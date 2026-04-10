package com.example.vocabanana.feature.database.language.lemmatiazation

import com.example.vocabanana.core.database.LemmatizationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LemmaRepositoryModule {
    @Binds
    abstract fun bindLemmatizationRepository(impl: LemmatizationRepositoryImpl): LemmatizationRepository
}