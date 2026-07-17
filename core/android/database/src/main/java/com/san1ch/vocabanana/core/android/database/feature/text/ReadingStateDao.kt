package com.san1ch.vocabanana.core.android.database.feature.text

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingStateDao {

    @Query("SELECT * FROM reading_states WHERE textId = :textId")
    suspend fun getReadingStateById(textId: Int): ReadingStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingState(state: ReadingStateEntity)

    @Query("SELECT * FROM reading_states WHERE textId = :id")
    fun getReadingStateByIdFlow(id: Int): Flow<ReadingStateEntity?>

    @Query("SELECT * FROM reading_states")
    fun getAllReadingStateFlow(): Flow<List<ReadingStateEntity>>
}
