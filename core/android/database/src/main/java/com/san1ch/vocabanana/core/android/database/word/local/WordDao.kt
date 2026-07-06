package com.san1ch.vocabanana.core.android.database.word.local

import androidx.room.*
import com.san1ch.vocabanana.core.android.database.word.model.WordToLemmaPair
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    // --- Flow Queries for UI Observation ---
    // Use these in ViewModels to get real-time updates
    @Transaction
    @Query("SELECT * FROM words")
    fun getAllWordsFlow(): Flow<List<WordWithForms>>

    @Transaction
    @Query("SELECT * FROM words WHERE id = :id")
    fun getWordWithFormsByIdFlow(id: Int): Flow<WordWithForms?>

    @Transaction
    @Query("SELECT * FROM words WHERE state IN (:stateValues)")
    fun getWordsByStates(stateValues: List<Int>): Flow<List<WordWithForms>>

    @Transaction
    @Query("SELECT * FROM words WHERE state NOT IN (:stateValues)")
    fun getWordsExceptStates(stateValues: List<Int>): Flow<List<WordWithForms>>


    @Query("SELECT COUNT(*) FROM words WHERE state NOT IN (:stateValues)")
    fun getCountExceptStates(stateValues: List<Int>): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE state IN (:stateValues)")
    fun getCountByStates(stateValues: List<Int>): Flow<Int>

    // --- Suspend Queries for Repository Logic ---
    // Use these inside Repository methods to avoid Flow overhead (.first())
    @Transaction
    @Query("SELECT * FROM words")
    suspend fun getAllWordsList(): List<WordWithForms>

    @Transaction
    @Query("SELECT * FROM words WHERE lemma IN (:lemmas)")
    suspend fun getWordsByLemmas(lemmas: List<String>): List<WordWithForms>
    @Transaction
    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordWithFormsById(id: Int): WordWithForms?

    @Transaction
    @Query("SELECT * FROM words WHERE lemma = :lemma")
    suspend fun getWordWithFormsByLemma(lemma: String): WordWithForms?

    @Query("""
    SELECT 'lemma' AS type, lemma AS word, lemma AS lemma FROM words WHERE lemma IN (:words)
    UNION
    SELECT 'form' AS type, form AS word, (SELECT lemma FROM words WHERE id = wordId) AS lemma FROM word_forms WHERE form IN (:words)
""")
    suspend fun getLemmasForWordsInternal(words: List<String>): List<WordToLemmaPair>
    //need query
    @Query("SELECT * FROM words WHERE lemma = :word")
    fun getWordsWithCount(word: String): Flow<List<WordWithForms>>

    @Transaction
    @Query("""
        SELECT * FROM words 
        WHERE lemma = :word 
        OR id IN (SELECT wordId FROM word_forms WHERE form = :word)
        LIMIT 1
    """)
    suspend fun getWordByAnyForm(word: String): WordWithForms?

    // --- Write Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertForms(forms: List<WordFormEntity>)

    /**
     * Inserts a word and its associated forms.
     * Deletes old forms first to ensure the list is exactly as provided.
     */
    @Transaction
    suspend fun insertWordWithForms(word: WordEntity, forms: List<String>) {
        val wordId = insertWord(word).toInt()
        deleteFormsForWord(wordId)
        val formEntities = forms.map { WordFormEntity(wordId, it) }
        insertForms(formEntities)
    }

    @Query("DELETE FROM word_forms WHERE wordId = :wordId")
    suspend fun deleteFormsForWord(wordId: Int)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Delete
    suspend fun deleteWord(word: WordEntity)

    @Query("DELETE FROM words")
    suspend fun deleteAll(): Int

}