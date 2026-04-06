package com.example.vocabanana.core.domain.module

import com.example.vocabanana.core.domain.TextToTextAiHub
import com.example.vocabanana.feature.ai.texttotext.TextToTextAiHubImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TextToTextAiHubModule {

    @Binds
    abstract fun bindTextToTextAiHub(textToTextAiHub: TextToTextAiHubImpl): TextToTextAiHub
}