package com.san1ch.vocabanana.feature.text.presentation

import com.san1ch.vocabanana.core.essentials.resources.featureproviders.TextStringProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TextModule {
    @Binds
    fun bindTextStringProvider(textStringProvider: TextStringProviderImpl): TextStringProvider
}
