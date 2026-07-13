package com.san1ch.vocabanana.core.android.database.word.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.san1ch.vocabanana.core.android.database.word.model.WordToLemmaPair
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    // --- Filter queries for Repository ---

    @Query(
        """
SELECT * FROM words
WHERE (:filterIds = 0 OR id IN (:idsIncluded))
AND (:excludeIds = 0 OR id NOT IN (:idsExcluded))
AND (:filterStates = 0 OR state IN (:statesIncluded))
AND (:excludeStates = 0 OR state NOT IN (:statesExcluded))
""",
    )
    fun getWordsFiltered(
        idsIncluded: List<Int>,
        idsExcluded: List<Int>,
        statesIncluded: List<Int>,
        statesExcluded: List<Int>,
        filterIds: Boolean,
        excludeIds: Boolean,
        filterStates: Boolean,
        excludeStates: Boolean,
    ): Flow<List<WordWithForms>>

    // --- Details and CRUD ---

    @Transaction
    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordWithFormsById(id: Int): WordWithForms?

    @Transaction
    @Query("SELECT * FROM words WHERE lemma = :lemma")
    suspend fun getWordWithFormsByLemma(lemma: String): WordWithForms?

    @Transaction
    @Query(
        """
        SELECT * FROM words
        WHERE lemma = :word
        OR id IN (SELECT wordId FROM word_forms WHERE form = :word)
        LIMIT 1
    """,
    )
    suspend fun getWordByAnyForm(word: String): WordWithForms?

    @Query("SELECT id FROM words WHERE lemma = :word")
    suspend fun getWordIdByAnyForm(word: String): Int?

    @Transaction
    @Query("SELECT * FROM words WHERE lemma IN (:lemmas)")
    suspend fun getWordsByLemmas(lemmas: List<String>): List<WordWithForms>

    @Transaction
    @Query("SELECT * FROM words")
    suspend fun getAllWordsList(): List<WordWithForms>

    @Query(
        """
        SELECT 'lemma' AS type, lemma AS word, lemma AS lemma FROM words WHERE lemma IN (:words)
        UNION
        SELECT 'form' AS type, form AS word, (SELECT lemma FROM words WHERE id = wordId) AS lemma FROM word_forms WHERE form IN (:words)
    """,
    )
    suspend fun getLemmasForWordsInternal(words: List<String>): List<WordToLemmaPair>

    // --- Write Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertForms(forms: List<WordFormEntity>)

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
