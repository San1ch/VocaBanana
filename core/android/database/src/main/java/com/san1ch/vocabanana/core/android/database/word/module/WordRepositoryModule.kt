package com.san1ch.vocabanana.core.android.database.word.module

import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import com.san1ch.vocabanana.core.android.database.word.repository.WordRepositoryRoomImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WordRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWordRepository(impl: WordRepositoryRoomImpl): WordRepository

}