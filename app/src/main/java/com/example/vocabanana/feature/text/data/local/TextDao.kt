package com.example.vocabanana.feature.text.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TextDao {
    @Query("SELECT * FROM texts")
    fun getTexts(): List<TextEntity>

    @Query("SELECT * FROM texts WHERE id = :id")
    fun getTextById(id: Int): TextEntity

    @Insert
    fun insertText(text: TextEntity)

    @Delete
    fun deleteText(text: TextEntity)
}