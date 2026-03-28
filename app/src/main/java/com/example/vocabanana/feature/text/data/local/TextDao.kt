package com.example.vocabanana.feature.text.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TextDao {
    @Query("SELECT * FROM texts")
    fun getTexts(): Flow<List<TextEntity>>

    @Query("SELECT * FROM texts WHERE id = :id")
    fun getTextById(id: Int): TextEntity

    @Insert
    fun insertText(text: TextEntity)

    @Delete
    fun deleteText(text: TextEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM texts WHERE name = :name)")
    fun isNameUnique(name: String): Boolean
}