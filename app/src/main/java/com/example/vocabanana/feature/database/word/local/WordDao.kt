package com.example.vocabanana.feature.database.word.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow


@Dao
interface WordDao {
    @Transaction
    @Query("SELECT * FROM words")
    fun getAllWords(): Flow<List<WordWithForms>>

    @Transaction
    @Query("SELECT * FROM words WHERE id = :id")
    fun getWordWithFormsById(id: Int): Flow<WordWithForms?>

    @Transaction
    @Query("SELECT * FROM words WHERE lemma = :lemma")
    fun getWordWithFormsByLemma(lemma: String): Flow<WordWithForms?>

    @Transaction
    @Query("""
        SELECT * FROM words 
        WHERE lemma = :word 
        OR id IN (SELECT wordId FROM word_forms WHERE form = :word)
        LIMIT 1
    """)
    fun getWordByAnyForm(word: String): Flow<WordWithForms?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertForms(forms: List<WordFormEntity>)

    @Transaction
    suspend fun insertWordWithForms(word: WordEntity, forms: List<String>) {
        // We delete old forms first to handle updates correctly
        val wordId = insertWord(word).toInt()
        deleteFormsForWord(wordId)
        val formEntities = forms.map { WordFormEntity(wordId, it) }
        insertForms(formEntities)
    }

    @Query("DELETE FROM word_forms WHERE wordId = :wordId")
    suspend fun deleteFormsForWord(wordId: Int)

    @Delete
    suspend fun deleteWord(word: WordEntity)

    @Query("DELETE FROM words")
    suspend fun deleteAll(): Int

    @Query("SELECT lemma FROM words WHERE lemma IN (:words)")
    suspend fun getExistingLemmas(words: List<String>): List<String>
}