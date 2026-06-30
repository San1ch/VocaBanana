package com.san1ch.vocabanana.core.android.database.language.lemmatiazation

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LemmaEntity::class], version = 1, exportSchema = false)
abstract class LemmatizationDatabase : RoomDatabase() {
    abstract fun lemmaDao(): LemmaDao
}