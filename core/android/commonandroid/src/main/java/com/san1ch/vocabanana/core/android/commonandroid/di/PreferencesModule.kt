package com.san1ch.vocabanana.core.android.commonandroid.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.san1ch.vocabanana.core.android.commonandroid.datastore.DataStoreBackupSettingsRepository
import com.san1ch.vocabanana.core.android.commonandroid.datastore.DataStoreGeneralSettingsRepository
import com.san1ch.vocabanana.core.android.commonandroid.datastore.DataStoreVocabularySettingsRepository
import com.san1ch.vocabanana.core.android.commonandroid.datastore.dataStore
import com.san1ch.vocabanana.core.essentials.repositories.BackupSettingsRepository
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
import com.san1ch.vocabanana.core.essentials.repositories.VocabularySettingsRepository
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
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideSettingsRepository(dataStore: DataStore<Preferences>): SettingsRepository = DataStoreGeneralSettingsRepository(dataStore)

    @Provides
    @Singleton
    fun provideBackupSettingsRepository(dataStore: DataStore<Preferences>): BackupSettingsRepository = DataStoreBackupSettingsRepository(dataStore)

    @Provides
    @Singleton
    fun provideVocabularySettingsRepository(dataStore: DataStore<Preferences>): VocabularySettingsRepository = DataStoreVocabularySettingsRepository(dataStore)
}
