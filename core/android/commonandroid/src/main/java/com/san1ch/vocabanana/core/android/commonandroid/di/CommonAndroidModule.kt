package com.san1ch.vocabanana.core.android.commonandroid.di

import com.san1ch.vocabanana.core.android.commonandroid.DataChangeTrackerImpl
import com.san1ch.vocabanana.core.android.commonandroid.datastore.DataStoreGoogleRepository
import com.san1ch.vocabanana.core.essentials.DataChangeTracker
import com.san1ch.vocabanana.core.essentials.exceptions.mapper.DefaultExceptionToMessageMapper
import com.san1ch.vocabanana.core.essentials.exceptions.mapper.ExceptionToMessageMapper
import com.san1ch.vocabanana.core.essentials.repositories.GoogleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CommonAndroidModule {

    @Binds
    fun bindExceptionToMessageMapper(
        impl: DefaultExceptionToMessageMapper,
    ): ExceptionToMessageMapper

    @Binds
    fun bindDataChangeTracker(
        impl: DataChangeTrackerImpl,
    ): DataChangeTracker

    @Binds
    @Singleton
    fun bindGoogleRepository(
        impl: DataStoreGoogleRepository,
    ): GoogleRepository
}
