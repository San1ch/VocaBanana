package com.example.vocabanana.feature.ai.texttotext.module

import com.example.vocabanana.feature.ai.texttotext.TextToTextAi
import com.example.vocabanana.feature.ai.texttotext.groq.GroqTextToTextAi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TextToTextAiModule {

    @Binds
    abstract fun bindTextToTextAi(groq: GroqTextToTextAi): TextToTextAi


}