package com.san1ch.vocabanana.core.android.database.text.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.san1ch.vocabanana.core.android.database.text.TextMetadataProjection
import kotlinx.coroutines.flow.Flow

@Dao
interface TextDao {
    @Query("SELECT * FROM texts")
    fun getTexts(): Flow<List<TextEntity>>

    @Query("SELECT * FROM texts WHERE id = :id")
    fun getTextByIdFlow(id: Int): Flow<TextEntity?>
    @Query("SELECT * FROM texts WHERE id IN (:ids)")
    suspend fun getTextsByIds(ids: List<Int>): List<TextEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTexts(texts: List<TextEntity>)

    @Query("DELETE FROM texts WHERE id IN (:textIds)")
    suspend fun deleteTextsByIds(textIds: List<Int>)

    @Query("SELECT EXISTS(SELECT 1 FROM texts WHERE name = :name)")
    fun isNameUnique(name: String): Boolean
}