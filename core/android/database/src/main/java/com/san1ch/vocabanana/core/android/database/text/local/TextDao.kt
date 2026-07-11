package com.san1ch.vocabanana.core.android.database.text.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TextDao {



    @Query("SELECT * FROM texts")
    fun getTexts(): Flow<List<TextEntity>>

    @Query("SELECT * FROM texts WHERE id = :id")
    fun getTextById(id: Int): TextEntity?

    @Insert
    fun insertText(text: TextEntity)

    @Query("DELETE FROM texts WHERE id = :textId")
    suspend fun deleteTextById(textId: Int)

    @Query("UPDATE texts SET last_scroll_position = :position, last_read_time = :time WHERE id = :id")
    suspend fun updateProgress(id: Int, position: Float, time: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM texts WHERE name = :name)")
    fun isNameUnique(name: String): Boolean

}