package com.example.vocabanana.feature.text.data.module

import com.example.vocabanana.feature.text.data.repository.TextRepositoryImpl
import com.example.vocabanana.feature.text.domain.TextRepository
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