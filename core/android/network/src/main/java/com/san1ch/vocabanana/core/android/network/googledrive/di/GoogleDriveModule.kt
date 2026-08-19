package com.san1ch.vocabanana.core.android.network.googledrive.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.san1ch.vocabanana.core.android.network.googledrive.GoogleApiStringProviderImpl
import com.san1ch.vocabanana.core.android.network.googledrive.GoogleAuthManagerImpl
import com.san1ch.vocabanana.core.android.network.googledrive.GoogleDriveApiService
import com.san1ch.vocabanana.core.android.network.googledrive.GoogleDriveConfig
import com.san1ch.vocabanana.core.android.network.googledrive.GoogleDriveService
import com.san1ch.vocabanana.core.android.network.googledrive.GoogleDriveTokenProviderImpl
import com.san1ch.vocabanana.core.android.network.googledrive.clients.createGoogleDriveOkHttpClient
import com.san1ch.vocabanana.core.android.network.googledrive.clients.createGoogleDriveRetrofit
import com.san1ch.vocabanana.core.android.network.googledrive.convertor.createGoogleDriveJson
import com.san1ch.vocabanana.core.essentials.backup.GoogleDriveTokenProvider
import com.san1ch.vocabanana.core.essentials.network.CloudStorageClient
import com.san1ch.vocabanana.core.essentials.network.GoogleAuthManager
import com.san1ch.vocabanana.core.essentials.resources.network.GoogleApiStringProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object GoogleDriveModule {
    @Provides
    @Singleton
    fun provideGoogleDriveJson(networkConfig: GoogleDriveConfig): Json {
        return createGoogleDriveJson(isDebug = networkConfig.isDebug)
    }

    @Provides
    @Singleton
    fun provideGoogleDriveOkHttpClient(networkConfig: GoogleDriveConfig): OkHttpClient {
        return createGoogleDriveOkHttpClient(timeout = networkConfig.timeout)
    }

    @Provides
    @Singleton
    fun provideGoogleDriveRetrofit(networkConfig: GoogleDriveConfig, okHttpClient: OkHttpClient, json: Json): Retrofit {
        return createGoogleDriveRetrofit(
            baseUrl = networkConfig.baseUrl,
            client = okHttpClient,
            json = json
        )
    }

    @Provides
    @Singleton
    fun provideGoogleDriveApiService(retrofit: Retrofit): GoogleDriveApiService {
        return retrofit.create(GoogleDriveApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface GoogleDriveBindModule{
    @Binds
    fun bindGoogleDriveService(service: GoogleDriveService): CloudStorageClient

    @Binds
    @Singleton
    fun bindGoogleDriveTokenManager(
        impl: GoogleDriveTokenProviderImpl,
    ): GoogleDriveTokenProvider

    @Binds
    @Singleton
    fun bindGoogleAuthManager(
        impl: GoogleAuthManagerImpl,
    ): GoogleAuthManager

    @Binds
    @Singleton
    fun bindGoogleApiStringProvider(
        impl: GoogleApiStringProviderImpl,
    ): GoogleApiStringProvider
}