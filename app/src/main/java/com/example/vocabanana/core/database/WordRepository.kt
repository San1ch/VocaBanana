package com.example.vocabanana.core.database

import com.example.vocabanana.feature.word.domain.model.WordDomain
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getAllWords(): Flow<List<WordDomain>>
    fun addWord(word: WordDomain)
    fun addWords(words: List<WordDomain>)
    fun removeWord(word: WordDomain)
    suspend fun deleteAll(): Int
    suspend fun getExistingWords(words: List<String>): Set<String>
}
