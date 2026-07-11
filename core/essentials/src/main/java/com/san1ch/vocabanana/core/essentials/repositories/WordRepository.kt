package com.san1ch.vocabanana.core.essentials.repositories

import com.san1ch.vocabanana.core.essentials.model.word.WordDomain
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import kotlinx.coroutines.flow.Flow

interface WordRepository {

    // --- Data Streams (Core) ---
    fun getWords(
        wordIds: FilterType<Int> = FilterType.All,
        states: FilterType<WordState> = FilterType.All
    ): Flow<List<WordDomain>>
    // --- Create / Add ---
    suspend fun addWords(words: List<WordDomain>)
    suspend fun getLemmasForWords(words: List<String>): Map<String, String>

    // --- Update ---
    suspend fun updateWord(word: WordDomain)
    suspend fun syncWordWithDatabase(word: WordDomain)
    suspend fun changeState(wordId: Int, state: WordState)

    // --- Get (Single or Static List) ---
    suspend fun getWordByWord(word: String): Result<WordDomain>
    suspend fun getWordById(id: Int): Result<WordDomain>

    suspend fun getWordDomainsForWords(words: List<String>): Map<String, WordDomain>
    suspend fun getAllLemmasAndForms(): List<String>
    suspend fun getExistingWords(words: List<String>): Set<String>

    // --- Delete ---
    suspend fun deleteWord(word: WordDomain)
    suspend fun deleteById(id: Int)
    suspend fun deleteAllWords(): Int
}