package com.example.vocabanana.feature.database.language.lexicon

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LexiconRepositoryModule {

    @Binds
    abstract fun bindLexiconRepository(impl: LexiconRepositoryImpl): LexiconRepository
}