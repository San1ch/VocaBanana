package com.example.vocabanana.feature.database.word.repository

import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.feature.database.word.local.WordDao
import com.example.vocabanana.feature.word.domain.model.WordDomain
import com.example.vocabanana.feature.word.mapper.toDomain
import com.example.vocabanana.feature.word.mapper.toWordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordRepositoryRoomImpl @Inject constructor(
    private val wordDao: WordDao,
) : WordRepository {
    override fun getAllLemmas(): Flow<List<WordDomain>> =
        wordDao.getAllWords().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getAllLemmasAndForms(): List<String> {
        val result = mutableListOf<String>()
        val flowData = wordDao.getAllWords().first()
        flowData.forEach { lemma ->
            result.add(lemma.lemma)
            lemma.forms.forEach { form ->
                result.add(form)
            }
        }
        return result
    }

    override suspend fun addWords(words: List<WordDomain>) {
        // 1. Get existing words from DB
        val databaseWordsMap = wordDao.getAllWords().first()
            .associateBy { it.lemma }

        // 2. This map will track the "current best version" of each word during processing
        val processingMap = mutableMapOf<String, WordDomain>()

        words.forEach { newWord ->
            // Check if we already handled this lemma in this loop OR if it's in the DB
            val existingEntry = processingMap[newWord.lemma]
                ?: databaseWordsMap[newWord.lemma]?.toDomain()

            if (existingEntry != null) {
                // Merge forms into the existing entry
                val updatedWord = existingEntry.addForms(newWord.forms)
                processingMap[newWord.lemma] = updatedWord
            } else {
                // First time seeing this lemma in both DB and this list
                processingMap[newWord.lemma] = newWord
            }
        }

        // 3. Convert the merged results to entities and save
        val finalEntities = processingMap.values.map { it.toWordEntity() }
        wordDao.insertWords(finalEntities)
    }

    override suspend fun updateOrAddWord(word: WordDomain) {
        val existingEntity = if (word.id != 0) {
            wordDao.getWordById(word.id).first()
        } else {
            wordDao.getWordByWord(word.lemma).first()
        }

        if (existingEntity != null) {
            val existingDomain = existingEntity.toDomain()
            val updatedDomain = existingDomain.addForms(word.forms)

            wordDao.insertWord(updatedDomain.toWordEntity())
        } else {
            wordDao.insertWord(word.toWordEntity())
        }
    }

    override fun removeWord(word: WordDomain) {
        wordDao.deleteWord(word.toWordEntity())
    }

    override suspend fun getExistingWords(words: List<String>): Set<String> {
        val existingLemmas = wordDao.getExistingWords(words)
        val existingForms = wordDao.getAllWords().first().flatMap { it.forms }

        return (existingLemmas + existingForms).toSet()
    }

    override suspend fun deleteAll(): Int {
        return wordDao.deleteAll()
    }

}
