package com.san1ch.vocabanana.core.android.database.text.module

import com.san1ch.vocabanana.core.essentials.database.repositories.TextRepository
import com.san1ch.vocabanana.core.android.database.text.repository.TextRepositoryImpl
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