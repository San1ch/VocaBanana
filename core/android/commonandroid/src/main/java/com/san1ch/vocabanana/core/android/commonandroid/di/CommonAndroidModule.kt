package com.san1ch.vocabanana.core.android.commonandroid.di

import com.san1ch.vocabanana.core.android.commonandroid.stringprovider.CoreStringProviderImpl
import com.san1ch.vocabanana.core.android.commonandroid.stringprovider.RepositoryStringProviderImpl
import com.san1ch.vocabanana.core.essentials.exceptions.mapper.DefaultExceptionToMessageMapper
import com.san1ch.vocabanana.core.essentials.exceptions.mapper.ExceptionToMessageMapper
import com.san1ch.vocabanana.core.essentials.resources.CoreStringProvider
import com.san1ch.vocabanana.core.essentials.resources.featureproviders.RepositoryStringProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CommonAndroidModule {
    @Binds
    fun bindStringResources(stringResourcesImpl: CoreStringProviderImpl): CoreStringProvider

    @Binds
    fun bindExceptionToMessageMapper(
        impl: DefaultExceptionToMessageMapper
    ): ExceptionToMessageMapper

    @Binds
    fun bindRepositoryStringProvider(
        impl: RepositoryStringProviderImpl
    ): RepositoryStringProvider
}