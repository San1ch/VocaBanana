package com.example.vocabanana.feature.database.word.module

import com.example.vocabanana.core.database.WordFormRepository
import com.example.vocabanana.feature.database.word.repository.WordFormRepositoryImpl
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.feature.database.word.repository.WordRepositoryRoomImpl
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