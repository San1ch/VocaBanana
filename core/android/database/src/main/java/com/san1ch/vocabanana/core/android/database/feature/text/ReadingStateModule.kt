package com.san1ch.vocabanana.core.android.database.feature.text

import com.san1ch.vocabanana.feature.text.domain.ReadingStateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReadingStateModule {

    @Binds
    @Singleton
    abstract fun bindReadingStateRepository(
        readingStateRepositoryImpl: ReadingStateRepositoryImpl
    ): ReadingStateRepository
}