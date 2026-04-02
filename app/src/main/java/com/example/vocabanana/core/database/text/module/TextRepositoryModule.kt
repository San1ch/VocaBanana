package com.example.vocabanana.core.database.text.module

import com.example.vocabanana.core.database.text.repository.TextRepository
import com.example.vocabanana.core.database.text.repository.TextRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TextRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTextRepository(impl: TextRepositoryImpl): TextRepository

}