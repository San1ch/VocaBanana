package com.san1ch.vocabanana.core.android.commonandroid.logger

import com.san1ch.vocabanana.core.essentials.Logger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LoggerModule {
    @Binds
    fun bindLogger(logger: AndroidLogger): Logger
}