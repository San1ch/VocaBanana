package com.example.vocabanana.feature.word.data.repository

import com.example.vocabanana.feature.word.data.WordDomain
import com.example.vocabanana.feature.word.data.local.WordDao
import com.example.vocabanana.feature.word.data.local.WordEntity
import com.example.vocabanana.feature.word.domain.WordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WordRepositoryRoomImpl @Inject constructor (private val dao: WordDao) : WordRepository {
    override fun getAllWords(): Flow<List<WordEntity>> = dao.getAllWords()
    override fun addWord(word: WordEntity) = dao.insertWord(word)
    override fun updateWord(word: WordEntity) = dao.updateWord(word)
    override fun removeWord(word: WordEntity) = dao.deleteWord(word)
}



fun WordEntity.toDomain() = WordDomain.createUnsafe(
    id = id,
    lemma = lemma,
    whenAdded = whenAdded
)