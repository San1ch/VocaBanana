package com.san1ch.vocabanana.core.android.commonandroid.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.san1ch.vocabanana.core.android.commonandroid.datastore.DataStoreSettingsRepository
import com.san1ch.vocabanana.core.android.commonandroid.datastore.dataStore
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(dataStore: DataStore<Preferences>): SettingsRepository {
        return DataStoreSettingsRepository(dataStore)
    }

}