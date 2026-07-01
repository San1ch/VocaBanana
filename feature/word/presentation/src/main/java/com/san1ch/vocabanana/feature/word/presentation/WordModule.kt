package com.san1ch.vocabanana.feature.word.presentation

import com.san1ch.vocabanana.core.essentials.resources.featureproviders.WordStringProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface WordModule {
    @Binds
    fun bindWordStringProvider(wordStringProvider: WordStringProviderImpl): WordStringProvider
}