package com.san1ch.vocabanana.core.android.database.text.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TextWordCountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordCounts(counts: List<TextWordCountEntity>)

    @Query("DELETE FROM text_word_counts WHERE textId = :textId")
    suspend fun deleteCountsForText(textId: Int)
    @Query("SELECT id FROM texts")
    suspend fun getAllTextIds(): List<Int>
    @Query("SELECT * FROM text_word_counts WHERE textId = :textId")
    suspend fun getWordCountsForText(textId: Int): List<TextWordCountEntity>
    
    @Query("SELECT * FROM text_word_counts WHERE textId = :textId AND wordId = :wordId")
    suspend fun getWordCountInSpecificText(textId: Int, wordId: Int): TextWordCountEntity

    @Query("SELECT * FROM text_word_counts WHERE wordId IN (:wordList)")
    suspend fun getWordCountsByWordList(wordList: List<Int>): List<TextWordCountEntity>
    @Query("SELECT * FROM text_word_counts WHERE wordId = :wordId")
    suspend fun getWordCount(wordId: Int): TextWordCountEntity
}