package com.example.vocabanana.feature.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vocabanana.feature.database.text.local.TextDao
import com.example.vocabanana.feature.database.text.local.TextEntity
import com.example.vocabanana.feature.database.word.local.WordConverters
import com.example.vocabanana.feature.database.word.local.WordDao
import com.example.vocabanana.feature.database.word.local.WordEntity

@Database(
    entities = [
        // [ENTITIES_START]
        WordEntity::class,
        TextEntity::class,
        // [ENTITIES_END]
    ],

    version = 5,
    exportSchema = false
)
@TypeConverters(WordConverters::class)
abstract class AppDatabase : RoomDatabase() {
    // [DAOS_START]
    abstract fun wordDao(): WordDao
    abstract fun textDao(): TextDao
    // [DAOS_END]

}