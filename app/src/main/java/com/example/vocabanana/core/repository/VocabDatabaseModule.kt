package com.example.vocabanana.core.repository

import android.content.Context
import androidx.room.Room
import com.example.vocabanana.feature.word.data.local.WordFormDao
import com.example.vocabanana.feature.word.data.local.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VocabDatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "vocab_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWordDao(appDatabase: AppDatabase): WordDao {
        return appDatabase.wordDao()
    }

    @Provides
    @Singleton
    fun provideWordFormDao(appDatabase: AppDatabase): WordFormDao {
        return appDatabase.wordFormDao()
    }
}