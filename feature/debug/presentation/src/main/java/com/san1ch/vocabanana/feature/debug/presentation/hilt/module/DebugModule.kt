package com.san1ch.vocabanana.feature.debug.presentation.hilt.module

import com.san1ch.vocabanana.feature.debug.domain.DebugAssistant
import com.san1ch.vocabanana.feature.debug.presentation.DebugAssistantImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DebugModule {
    @Binds
    fun bindDebugAssistant(impl: DebugAssistantImpl): DebugAssistant
}