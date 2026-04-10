package com.example.vocabanana.feature.database.language.lexicon

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LexiconEntity::class], version = 1)
abstract class LexiconDatabase : RoomDatabase() {
    abstract fun lexiconDao(): LexiconDao
}