package com.example.vocabanana.feature.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vocabanana.feature.database.text.local.TextDao
import com.example.vocabanana.feature.database.text.local.TextEntity
import com.example.vocabanana.feature.database.word.local.WordDao
import com.example.vocabanana.feature.database.word.local.WordEntity
import com.example.vocabanana.feature.database.word.local.WordFormDao
import com.example.vocabanana.feature.database.word.local.WordFormEntity

@Database(
    entities = [
        // [ENTITIES_START]
        WordEntity::class,
        WordFormEntity::class,
        TextEntity::class,
        // [ENTITIES_END]
    ],

    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // [DAOS_START]
    abstract fun wordDao(): WordDao
    abstract fun wordFormDao(): WordFormDao
    abstract fun textDao(): TextDao
    // [DAOS_END]

}