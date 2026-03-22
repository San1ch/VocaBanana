package com.example.vocabanana.core.repository.wordrepository.module

import com.example.vocabanana.core.repository.wordrepository.WordFormRepository
import com.example.vocabanana.core.repository.wordrepository.WordRepository
import com.example.vocabanana.core.repository.wordrepository.room.form.WordFormRepositoryRoomImpl
import com.example.vocabanana.core.repository.wordrepository.room.word.WordRepositoryRoomImpl
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
    abstract fun bindWordFormRepository(impl: WordFormRepositoryRoomImpl): WordFormRepository
}