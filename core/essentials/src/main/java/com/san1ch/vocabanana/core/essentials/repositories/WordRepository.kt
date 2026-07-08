package com.san1ch.vocabanana.core.essentials.repositories

import com.san1ch.vocabanana.core.essentials.model.word.WordDomain
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    // --- Data Streams (Flows) ---
    fun getAllLemmas(): Flow<List<WordDomain>>
    fun getWordsByStates(states: List<WordState>): Flow<List<WordDomain>>
    fun getWordExceptStates(states: List<WordState>): Flow<List<WordDomain>>
    fun getCountByStates(states: List<WordState>): Flow<Int>
    fun getCountExceptStates(states: List<WordState>): Flow<Int>
    // --- Create / Add ---
    suspend fun addWords(words: List<WordDomain>)
    suspend fun getLemmasForWords(word: List<String>): Map<String, String>
    // --- Update ---
    /**
     * Lightweight update. Use this for changing simple properties like 'state'.
     * It only updates the word entry itself, skipping the associated forms.
     */
    suspend fun updateWord(word: WordDomain)

    /**
     * Heavy update/merge. Use this when parsing text or importing.
     * It merges new forms with existing ones and updates the full relation.
     */
    suspend fun syncWordWithDatabase(word: WordDomain)

    // --- Get (Single or Static List) ---
    suspend fun changeState(wordId: Int, state: WordState)
    suspend fun getWordByWord(word: String): Result<WordDomain>
    suspend fun getWordById(id: Int): Result<WordDomain>
    suspend fun getAllLemmasAndForms(): List<String>
    suspend fun getExistingWords(words: List<String>): Set<String>
    suspend fun getWordDomainsForWords(words: List<String>): Map<String, WordDomain>

    // --- Delete ---
    suspend fun deleteWord(word: WordDomain)
    suspend fun deleteById(id: Int)
    suspend fun deleteAllWords(): Int
}