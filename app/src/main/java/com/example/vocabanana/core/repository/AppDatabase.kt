package com.example.vocabanana.core.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vocabanana.feature.text.data.local.TextDao
import com.example.vocabanana.feature.text.data.local.TextEntity
import com.example.vocabanana.feature.word.data.local.WordDao
import com.example.vocabanana.feature.word.data.local.WordEntity
import com.example.vocabanana.feature.word.data.local.WordFormDao
import com.example.vocabanana.feature.word.data.local.WordFormsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(
    entities = [WordEntity::class, WordFormsEntity::class, TextEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {



    suspend fun clearDebugData(database: AppDatabase) {
        withContext(Dispatchers.IO) {
            database.clearAllTables()
        }
    }

    abstract fun wordDao(): WordDao
    abstract fun wordFormDao(): WordFormDao
    abstract fun textDao(): TextDao
}