package com.example.vocabanana.core.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vocabanana.feature.text.data.local.TextDao
import com.example.vocabanana.feature.text.data.local.TextEntity
import com.example.vocabanana.feature.word.data.local.WordDao
import com.example.vocabanana.feature.word.data.local.WordEntity
import com.example.vocabanana.feature.word.data.local.WordFormDao
import com.example.vocabanana.feature.word.data.local.WordFormEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(
    entities = [
        // [ENTITIES_START]
        WordEntity::class,
        WordFormEntity::class,
        TextEntity::class,
        // [ENTITIES_END]
    ],

    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // [DAOS_START]
    abstract fun wordDao(): WordDao
    abstract fun wordFormDao(): WordFormDao
    abstract fun textDao(): TextDao
    // [DAOS_END]

}