package com.example.vocabanana.core.database.word.repository

import com.example.vocabanana.feature.word.domain.WordDomain
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getAllWords(): Flow<List<WordDomain>>
    fun addWord(word: WordDomain)
    fun removeWord(word: WordDomain)
    suspend fun getExistingWords(words: List<String>): Set<String>
}
