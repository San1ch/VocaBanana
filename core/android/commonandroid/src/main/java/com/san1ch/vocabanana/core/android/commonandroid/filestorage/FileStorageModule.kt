package com.san1ch.vocabanana.core.android.commonandroid.filestorage

import com.san1ch.vocabanana.core.essentials.repositories.FileStorage
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