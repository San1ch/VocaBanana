package com.san1ch.vocabanana.core.android.database

import com.san1ch.vocabanana.core.android.database.language.lemmatiazation.LemmatizationRepositoryImpl
import com.san1ch.vocabanana.core.android.database.language.lexicon.LexiconRepositoryImpl
import com.san1ch.vocabanana.core.android.database.text.repository.TextRepositoryImpl
import com.san1ch.vocabanana.core.android.database.word.repository.WordRepositoryImpl
import com.san1ch.vocabanana.core.essentials.repositories.LemmatizationRepository
import com.san1ch.vocabanana.core.essentials.repositories.LexiconRepository
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Binds
    @Singleton
    abstract fun bindWordRepository(impl: WordRepositoryImpl): WordRepository

    @Binds
    @Singleton
    abstract fun bindTextRepository(impl: TextRepositoryImpl): TextRepository

    @Binds
    abstract fun bindLexiconRepository(impl: LexiconRepositoryImpl): LexiconRepository

    @Binds
    abstract fun bindLemmatizationRepository(impl: LemmatizationRepositoryImpl): LemmatizationRepository
}
