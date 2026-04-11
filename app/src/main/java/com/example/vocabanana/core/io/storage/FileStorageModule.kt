package com.example.vocabanana.core.io.storage

import com.example.vocabanana.android.AndroidFileStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FileStorageModule {

    @Binds
    abstract fun providesFileStorage(impl: AndroidFileStorage): FileStorage
}