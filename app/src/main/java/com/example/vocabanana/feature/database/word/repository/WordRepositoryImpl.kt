package com.example.vocabanana.feature.database.word.repository

import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.feature.database.word.local.WordDao
import com.example.vocabanana.feature.database.word.local.WordFormDao
import com.example.vocabanana.feature.word.domain.model.WordDomain
import com.example.vocabanana.feature.word.mapper.toDomain
import com.example.vocabanana.feature.word.mapper.toEntity
import com.example.vocabanana.feature.word.mapper.toWordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordRepositoryRoomImpl @Inject constructor(
    private val wordDao: WordDao,
    private val formDao: WordFormDao
) : WordRepository {
    override fun getAllLemmas(): Flow<List<WordDomain>> =
        wordDao.getAllWords().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getAllLemmasAndForms(): List<String> {
        val result = mutableListOf<String>()
        wordDao.getAllWords().collect { flow ->
            flow.forEach { lemma ->
                result.add(lemma.word.lemma)
                lemma.forms.forEach { form ->
                    result.add(form.form)
                }
            }
        }

    }

    override fun addWord(word: WordDomain) {
        val wordEntity = word.toWordEntity()

        wordDao.insertWord(wordEntity)

        val wordId = wordEntity.id

        val formEntities = word.forms.map {
            it.toEntity(wordId)
        }

        formDao.insertWordForms(formEntities)
    }

    override fun removeWord(word: WordDomain) {
        wordDao.deleteWord(word.toWordEntity())
    }

    override suspend fun getExistingWords(words: List<String>): Set<String> {
        val existingLemmas = wordDao.getExistingWords(words)
        val existingForms = formDao.getExistingForms(words)

        return (existingLemmas + existingForms).toSet()
    }

    override suspend fun deleteAll(): Int {
        return wordDao.deleteAll()
    }

    override fun addWords(words: List<WordDomain>) {
        wordDao.insertWords(words.map { it.toWordEntity() })
    }
}
