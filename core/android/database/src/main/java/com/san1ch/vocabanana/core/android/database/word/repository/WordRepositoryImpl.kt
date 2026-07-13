package com.san1ch.vocabanana.core.android.database.word.repository

import com.san1ch.vocabanana.core.android.database.word.local.WordDao
import com.san1ch.vocabanana.core.android.database.word.model.WordToLemmaPair
import com.san1ch.vocabanana.core.android.database.word.toDomain
import com.san1ch.vocabanana.core.android.database.word.toWordEntity
import com.san1ch.vocabanana.core.essentials.exceptions.RepositoryNoDataByRequestException
import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import com.san1ch.vocabanana.core.essentials.model.word.WordDomain
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
) : WordRepository {

    override fun getWords(
        wordIds: FilterType<Int>,
        states: FilterType<WordState>,
    ): Flow<List<WordDomain>> {
        val idsIncluded = when (wordIds) {
            is FilterType.Include -> wordIds.items
            else -> emptyList()
        }

        val idsExcluded = when (wordIds) {
            is FilterType.Exclude -> wordIds.items
            else -> emptyList()
        }

        val statesIncluded = when (states) {
            is FilterType.Include -> states.items.map { it.value }
            else -> emptyList()
        }

        val statesExcluded = when (states) {
            is FilterType.Exclude -> states.items.map { it.value }
            else -> emptyList()
        }

        return wordDao.getWordsFiltered(
            idsIncluded = idsIncluded,
            idsExcluded = idsExcluded,
            statesIncluded = statesIncluded,
            statesExcluded = statesExcluded,
            filterIds = wordIds is FilterType.Include,
            excludeIds = wordIds is FilterType.Exclude,
            filterStates = states is FilterType.Include,
            excludeStates = states is FilterType.Exclude,
        )
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun updateWord(word: WordDomain) {
        wordDao.insertWord(word.toWordEntity())
    }

    override suspend fun syncWordWithDatabase(word: WordDomain) {
        val existing = if (word.id != 0) {
            wordDao.getWordWithFormsById(word.id)
        } else {
            wordDao.getWordWithFormsByLemma(word.lemma)
        }

        if (existing != null) {
            val updated = existing.toDomain().addForms(word.forms)
            wordDao.insertWordWithForms(updated.toWordEntity(), updated.forms)
        } else {
            wordDao.insertWordWithForms(word.toWordEntity(), word.forms)
        }
    }

    override suspend fun changeState(wordId: Int, state: WordState) {
        wordDao.getWordWithFormsById(wordId)?.let {
            val updated = it.toDomain().withState(state)
            wordDao.insertWord(updated.toWordEntity())
        }
    }

    override suspend fun addWords(words: List<WordDomain>) {
        val dbWords = wordDao.getAllWordsList().associateBy { it.word.lemma }
        val toSave = words.fold(mutableMapOf<String, WordDomain>()) { acc, newWord ->
            val existing = acc[newWord.lemma] ?: dbWords[newWord.lemma]?.toDomain()
            acc[newWord.lemma] = existing?.addForms(newWord.forms) ?: newWord
            acc
        }
        toSave.values.forEach { wordDao.insertWordWithForms(it.toWordEntity(), it.forms) }
    }

    override suspend fun getWordById(id: Int): Result<WordDomain> = wordDao.getWordWithFormsById(id)?.let { Result.success(it.toDomain()) }
        ?: Result.failure(RepositoryNoDataByRequestException())

    override suspend fun getWordByWord(word: String): Result<WordDomain> = wordDao.getWordByAnyForm(word.trim().lowercase())?.let {
        Result.success(it.toDomain())
    } ?: Result.failure(RepositoryNoDataByRequestException())

    override suspend fun getIdByWord(word: String): Result<Int> = wordDao.getWordIdByAnyForm(word.trim().lowercase())?.let { Result.success(it) }
        ?: Result.failure(RepositoryNoDataByRequestException())

    override suspend fun getLemmasForWords(words: List<String>): Map<String, String> {
        if (words.isEmpty()) return emptyMap()
        val databasePairs: List<WordToLemmaPair> = wordDao.getLemmasForWordsInternal(words)
        return databasePairs.associate { pair -> pair.word to pair.lemma }
    }

    override suspend fun getAllLemmasAndForms(): List<String> = wordDao.getAllWordsList().flatMap { item ->
        listOf(item.word.lemma) + item.forms.map { it.form }
    }

    override suspend fun getExistingWords(words: List<String>): Set<String> {
        val allData = wordDao.getAllWordsList()
        val lemmas = allData.map { it.word.lemma }.filter { it in words }
        val forms = allData.flatMap { it.forms.map { f -> f.form } }.filter { it in words }
        return (lemmas + forms).toSet()
    }

    override suspend fun getWordDomainsForWords(words: List<String>): Map<String, WordDomain> = wordDao.getWordsByLemmas(words).map { it.toDomain() }.associateBy { it.lemma }

    override suspend fun deleteWord(word: WordDomain) = wordDao.deleteWord(word.toWordEntity())

    override suspend fun deleteById(id: Int) = wordDao.deleteById(id)

    override suspend fun deleteAllWords(): Int = wordDao.deleteAll()
}
