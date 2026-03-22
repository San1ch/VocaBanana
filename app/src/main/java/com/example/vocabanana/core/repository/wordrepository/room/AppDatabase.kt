package com.example.vocabanana.core.repository.wordrepository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vocabanana.core.repository.wordrepository.room.form.WordFormDao
import com.example.vocabanana.core.repository.wordrepository.room.form.WordFormsEntity
import com.example.vocabanana.core.repository.wordrepository.room.word.WordDao
import com.example.vocabanana.core.repository.wordrepository.room.word.WordEntity

@Database(
    entities = [WordEntity::class, WordFormsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun wordFormDao(): WordFormDao
}