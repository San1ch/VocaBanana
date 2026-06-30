package com.san1ch.vocabanana.feature.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.san1ch.vocabanana.core.android.database.text.local.TextDao
import com.san1ch.vocabanana.core.android.database.text.local.TextEntity
import com.san1ch.vocabanana.feature.database.word.local.WordDao
import com.san1ch.vocabanana.feature.database.word.local.WordEntity
import com.san1ch.vocabanana.feature.database.word.local.WordFormEntity

@Database(
    entities = [
        // [ENTITIES_START]
        WordEntity::class,
        TextEntity::class,
        WordFormEntity::class,
        // [ENTITIES_END]
    ],

    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // [DAOS_START]
    abstract fun wordDao(): WordDao
    abstract fun textDao(): TextDao
    // [DAOS_END]

}