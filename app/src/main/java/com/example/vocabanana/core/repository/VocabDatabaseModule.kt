package com.example.vocabanana.core.repository

import com.example.vocabanana.feature.newabcdef.data.local.TextElementDao
import android.content.Context
import androidx.room.Room
import com.example.vocabanana.feature.text.data.local.TextDao
import com.example.vocabanana.feature.word.data.local.WordDao
import com.example.vocabanana.feature.word.data.local.WordFormDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.vocabanana.feature.newabcdef.data.local.BooBooBoooDao

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
        
@Provides
@Singleton
fun provideTextElementDao(appDatabase: AppDatabase): TextElementDao {
    return appDatabase.textelementDao()
}
        
@Provides
@Singleton
fun provideBooBooBoooDao(appDatabase: AppDatabase): BooBooBoooDao {
    return appDatabase.booBooBoooDao()
}
// [PROVIDES_END]
}