package com.san1ch.vocabanana.core.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.san1ch.vocabanana.core.android.database.text.local.TextDao
import com.san1ch.vocabanana.core.android.database.text.local.TextEntity
import com.san1ch.vocabanana.core.android.database.text.local.TextWordCountDao
import com.san1ch.vocabanana.core.android.database.text.local.TextWordCountEntity
import com.san1ch.vocabanana.core.android.database.word.local.WordDao
import com.san1ch.vocabanana.core.android.database.word.local.WordEntity
import com.san1ch.vocabanana.core.android.database.word.local.WordFormEntity
import dagger.Provides

@Database(
    entities = [
        WordEntity::class,
        TextEntity::class,
        WordFormEntity::class,
        TextWordCountEntity::class,
    ],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun textWordCountDao(): TextWordCountDao
    abstract fun textDao(): TextDao
}