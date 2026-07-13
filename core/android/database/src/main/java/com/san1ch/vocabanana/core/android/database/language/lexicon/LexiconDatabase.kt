package com.san1ch.vocabanana.core.android.database.language.lexicon

import androidx.room.Database
import androidx.room.RoomDatabase
import com.san1ch.vocabanana.feature.database.language.lexicon.LexiconDao

@Database(entities = [LexiconEntity::class], version = 1)
abstract class LexiconDatabase : RoomDatabase() {
    abstract fun lexiconDao(): LexiconDao
}
