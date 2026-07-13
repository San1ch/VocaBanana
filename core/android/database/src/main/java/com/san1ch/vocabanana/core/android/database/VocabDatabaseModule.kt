package com.san1ch.vocabanana.core.android.database

import android.content.Context
import androidx.room.Room
import com.san1ch.vocabanana.core.android.database.language.lemmatiazation.LemmaDao
import com.san1ch.vocabanana.core.android.database.language.lemmatiazation.LemmatizationDatabase
import com.san1ch.vocabanana.core.android.database.language.lexicon.LexiconDatabase
import com.san1ch.vocabanana.core.android.database.text.local.TextDao
import com.san1ch.vocabanana.core.android.database.text.local.TextWordCountDao
import com.san1ch.vocabanana.core.android.database.word.local.WordDao
import com.san1ch.vocabanana.feature.database.language.lexicon.LexiconDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "vocab_database",
    ).fallbackToDestructiveMigration(false).build()

    @Provides
    @Singleton
    fun provideLemmatizationDatabase(@ApplicationContext context: Context): LemmatizationDatabase = Room.databaseBuilder(
        context,
        LemmatizationDatabase::class.java,
        "external_lemmas.db",
    )
        .createFromAsset("english-lemmatization.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideLemmaDao(db: LemmatizationDatabase): LemmaDao = db.lemmaDao()

    @Provides
    @Singleton
    fun provideLexiconDatabase(@ApplicationContext context: Context): LexiconDatabase = Room.databaseBuilder(
        context,
        LexiconDatabase::class.java,
        "external_lexicon.db",
    )
        .createFromAsset("english-lexicon.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideLexiconDao(db: LexiconDatabase): LexiconDao = db.lexiconDao()

    // [PROVIDES_START]
    @Provides
    @Singleton
    fun provideWordDao(appDatabase: AppDatabase): WordDao = appDatabase.wordDao()

    @Provides
    @Singleton
    fun provideTextDao(appDatabase: AppDatabase): TextDao = appDatabase.textDao()

    @Provides
    fun provideTextWordCountDao(appDatabase: AppDatabase): TextWordCountDao = appDatabase.textWordCountDao()
// [PROVIDES_END]
}
