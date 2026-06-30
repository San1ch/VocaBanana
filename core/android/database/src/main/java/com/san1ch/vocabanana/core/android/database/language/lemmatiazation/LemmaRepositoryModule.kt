package com.san1ch.vocabanana.core.android.database.language.lemmatiazation

import com.san1ch.vocabanana.core.essentials.database.repositories.LemmatizationRepository
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