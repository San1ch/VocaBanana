package com.example.vocabanana.core.repository.wordrepository

import com.example.vocabanana.core.repository.wordrepository.room.word.WordEntity
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getAllWords(): Flow<List<WordEntity>>
    fun addWord(word: WordEntity)
    fun updateWord(word: WordEntity)
    fun removeWord(word: WordEntity)
}
