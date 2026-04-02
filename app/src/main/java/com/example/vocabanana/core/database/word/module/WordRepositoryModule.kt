package com.example.vocabanana.core.database.word.module

import com.example.vocabanana.core.database.word.repository.WordFormRepositoryImpl
import com.example.vocabanana.core.database.word.repository.WordRepositoryRoomImpl
import com.example.vocabanana.core.database.word.repository.WordFormRepository
import com.example.vocabanana.core.database.word.repository.WordRepository
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

    @Binds
    @Singleton
    abstract fun bindWordFormRepository(impl: WordFormRepositoryImpl): WordFormRepository
}