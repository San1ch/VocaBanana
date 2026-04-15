package com.example.vocabanana.core.database

import com.example.vocabanana.feature.word.domain.model.WordDomain
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getAllLemmas(): Flow<List<WordDomain>>
    suspend fun getAllLemmasAndForms(): List<String>
    suspend fun updateOrAddWord(word: WordDomain)
    suspend fun getWordByWord(word: String): WordDomain?
    suspend fun addWords(words: List<WordDomain>)
    suspend fun removeWord(word: WordDomain)
    suspend fun deleteAll(): Int
    suspend fun getExistingWords(words: List<String>): Set<String>
}
