package com.example.vocabanana.feature.database.word.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface WordDao {

    @Transaction
    @Query("SELECT * FROM words")
    fun getAllWords(): Flow<List<WordEntity>>

    @Transaction
    @Query("SELECT * FROM words WHERE id = :id")
    fun getWordById(id: Int): Flow<WordEntity?>


    @Query("SELECT * FROM words WHERE lemma = :lemmas")
    fun getAllWordByLemma(lemmas: List<String>): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE lemma = :lemma")
    fun wordExists(lemma: String): Boolean

    @Query("SELECT * FROM words")
    fun getAllWordsFlow(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE lemma = :lemma")
    fun getWordByWord(lemma: String): Flow<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: WordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWords(words: List<WordEntity>)

    @Update
    fun updateWord(word: WordEntity)

    @Delete
    fun deleteWord(word: WordEntity)

    @Query("DELETE FROM words")
    suspend fun deleteAll(): Int

    @Query("SELECT lemma FROM words WHERE lemma IN (:words)")
    fun getExistingWords(words: List<String>): List<String>
}
