package com.example.vocabanana.feature.database

import android.content.Context
import androidx.room.Room
import com.example.vocabanana.feature.database.language.lemmatiazation.LemmaDao
import com.example.vocabanana.feature.database.language.lemmatiazation.LemmatizationDatabase
import com.example.vocabanana.feature.database.language.lexicon.LexiconDao
import com.example.vocabanana.feature.database.language.lexicon.LexiconDatabase
import com.example.vocabanana.feature.database.text.local.TextDao
import com.example.vocabanana.feature.database.word.local.WordDao
import com.example.vocabanana.feature.database.word.local.WordFormDao
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

    @Provides
    @Singleton
    fun provideLemmatizationDatabase(@ApplicationContext context: Context): LemmatizationDatabase {
        return Room.databaseBuilder(
            context,
            LemmatizationDatabase::class.java,
            "external_lemmas.db"
        )
            .createFromAsset("english-lemmatization.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    fun provideLemmaDao(db: LemmatizationDatabase): LemmaDao = db.lemmaDao()

    @Provides
    @Singleton
    fun provideLexiconDatabase(@ApplicationContext context: Context): LexiconDatabase {
        return Room.databaseBuilder(
            context,
            LexiconDatabase::class.java,
            "external_lexicon.db"
        )
            .createFromAsset("english-lexicon.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    fun provideLexiconDao(db: LexiconDatabase): LexiconDao = db.lexiconDao()

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