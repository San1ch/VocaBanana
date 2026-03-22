package com.example.vocabanana.core.repository.wordrepository.room.word

import com.example.vocabanana.core.repository.wordrepository.WordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WordRepositoryRoomImpl @Inject constructor (private val dao: WordDao) : WordRepository {
    override fun getAllWords(): Flow<List<WordEntity>> = dao.getAllWords()
    override fun addWord(word: WordEntity) = dao.insertWord(word)
    override fun updateWord(word: WordEntity) = dao.updateWord(word)
    override fun removeWord(word: WordEntity) = dao.deleteWord(word)
}