package com.example.vocabanana.feature.word.domain

import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getAllWords(): Flow<List<WordDomain>>
    fun addWord(word: WordDomain)
    fun removeWord(word: WordDomain)
}
