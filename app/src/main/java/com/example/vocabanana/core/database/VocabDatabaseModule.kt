package com.example.vocabanana.core.database

import android.content.Context
import androidx.room.Room
import com.example.vocabanana.core.database.text.local.TextDao
import com.example.vocabanana.core.database.word.local.WordDao
import com.example.vocabanana.core.database.word.local.WordFormDao
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
        ).fallbackToDestructiveMigration(false).build()
    }
    // [PROVIDES_START]
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

    @Provides
    @Singleton
    fun provideTextDao(appDatabase: AppDatabase): TextDao {
        return appDatabase.textDao()
    }


// [PROVIDES_END]
}