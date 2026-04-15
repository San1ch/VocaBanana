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
        val data = wordDao.getAllWords().first()
        return data.flatMap { item ->
            listOf(item.word.lemma) + item.forms.map { it.form }
        }
    }

    override suspend fun getWordByWord(word: String): WordDomain? {
        val cleanWord = word.trim().lowercase()
        return wordDao.getWordByAnyForm(cleanWord).first()?.toDomain()
    }

    override suspend fun addWords(words: List<WordDomain>) {
        // 1. Get current DB snapshot
        val databaseWordsMap = wordDao.getAllWords().first()
            .associateBy { it.word.lemma }

        val processingMap = mutableMapOf<String, WordDomain>()

        words.forEach { newWord ->
            val existingEntry = processingMap[newWord.lemma]
                ?: databaseWordsMap[newWord.lemma]?.toDomain()

            if (existingEntry != null) {
                processingMap[newWord.lemma] = existingEntry.addForms(newWord.forms)
            } else {
                processingMap[newWord.lemma] = newWord
            }
        }

        // 2. Save merged results
        processingMap.values.forEach { domain ->
            wordDao.insertWordWithForms(domain.toWordEntity(), domain.forms)
        }
    }

    override suspend fun updateOrAddWord(word: WordDomain) {
        val existingRelation = if (word.id != 0) {
            wordDao.getWordWithFormsById(word.id).first()
        } else {
            wordDao.getWordWithFormsByLemma(word.lemma).first()
        }

        val domainToSave = if (existingRelation != null) {
            existingRelation.toDomain().addForms(word.forms)
        } else {
            word
        }

        wordDao.insertWordWithForms(domainToSave.toWordEntity(), domainToSave.forms)
    }

    override suspend fun removeWord(word: WordDomain) {
        // Room cascades the delete to word_forms automatically
        wordDao.deleteWord(word.toWordEntity())
    }

    override suspend fun getExistingWords(words: List<String>): Set<String> {
        val allData = wordDao.getAllWords().first()
        val lemmas = allData.map { it.word.lemma }.filter { it in words }
        val forms = allData.flatMap { it.forms.map { f -> f.form } }.filter { it in words }

        return (lemmas + forms).toSet()
    }

    override suspend fun deleteAll(): Int {
        return wordDao.deleteAll()
    }
}