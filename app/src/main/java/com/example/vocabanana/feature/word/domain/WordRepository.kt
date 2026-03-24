package com.example.vocabanana.feature.word.domain

import com.example.vocabanana.feature.word.data.local.WordEntity
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getAllWords(): Flow<List<WordEntity>>
    fun addWord(word: WordEntity)
    fun updateWord(word: WordEntity)
    fun removeWord(word: WordEntity)
}
