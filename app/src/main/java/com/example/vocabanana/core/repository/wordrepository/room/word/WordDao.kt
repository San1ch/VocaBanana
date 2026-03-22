package com.example.vocabanana.core.repository.wordrepository.room.word

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun getAllWordsFlow(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :id")
    fun getWordById(id: Int): WordEntity


    @Query("SELECT * FROM words WHERE word = :word")
    fun getWordByWord(word: String): WordEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: WordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWords(words: List<WordEntity>)

    @Update
    fun updateWord(word: WordEntity)

    @Delete
    fun deleteWord(word: WordEntity)
}
