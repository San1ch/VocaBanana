package com.san1ch.vocabanana

import com.san1ch.vocabanana.core.android.network.googledrive.GoogleDriveConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    fun provideGoogleDriveNetworkConfig() = GoogleDriveConfig(isDebug = BuildConfig.DEBUG)
}
