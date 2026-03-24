package com.example.vocabanana.feature.word.data.module

import com.example.vocabanana.feature.word.data.repository.WordFormRepositoryImpl
import com.example.vocabanana.feature.word.data.repository.WordRepositoryRoomImpl
import com.example.vocabanana.feature.word.domain.WordFormRepository
import com.example.vocabanana.feature.word.domain.WordRepository
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