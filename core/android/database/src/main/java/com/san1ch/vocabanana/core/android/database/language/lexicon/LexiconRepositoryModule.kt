package com.san1ch.vocabanana.core.android.database.language.lexicon

import com.san1ch.vocabanana.core.essentials.repositories.LexiconRepository
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