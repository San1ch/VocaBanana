package com.san1ch.vocabanana.core.android.database.word.repository

import com.san1ch.vocabanana.core.android.database.word.toDomain
import com.san1ch.vocabanana.core.android.database.word.toWordEntity
import com.san1ch.vocabanana.core.essentials.database.exceptions.RepositoryNoDataByRequestException
import com.san1ch.vocabanana.core.essentials.database.model.word.WordDomain
import com.san1ch.vocabanana.core.essentials.database.model.word.WordState
import com.san1ch.vocabanana.core.essentials.database.repositories.WordRepository
import com.san1ch.vocabanana.feature.database.word.local.WordDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordRepositoryRoomImpl @Inject constructor(
    private val wordDao: WordDao
) : WordRepository {

    // Helper to convert Enum list to Int list for Room queries
    private fun List<WordState>.toIntList() = this.map { it.value }

    // --- State-Based Queries (Flows for UI) ---

    override fun getAllLemmas(): Flow<List<WordDomain>> =
        wordDao.getAllWordsFlow()
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }

    override fun getWordByStates(states: List<WordState>): Flow<List<WordDomain>> =
        wordDao.getWordsByStates(states.toIntList())
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }

    override fun getWordExceptStates(states: List<WordState>): Flow<List<WordDomain>> =
        wordDao.getWordsExceptStates(states.toIntList())
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }

    override fun getCountExceptStates(states: List<WordState>): Flow<Int> =
        wordDao.getCountExceptStates(states.toIntList())
            .distinctUntilChanged()

    override fun getCountByStates(states: List<WordState>): Flow<Int> =
        wordDao.getCountByStates(states.toIntList())
            .distinctUntilChanged()


    // --- Detail & CRUD ---

    override suspend fun getWordById(id: Int): Result<WordDomain> {
        val result = wordDao.getWordWithFormsById(id)
        return if (result != null) {
            Result.success(result.toDomain())
        } else {
            Result.failure(RepositoryNoDataByRequestException())
        }
    }

    override suspend fun getWordByWord(word: String): Result<WordDomain> {
        val result = wordDao.getWordByAnyForm(word.trim().lowercase())
        return if (result != null) {
            Result.success(result.toDomain())
        } else {
            Result.failure(RepositoryNoDataByRequestException())
        }
    }

    /**
     * Optimized update: Use this for changing WordState (colors).
     * It only touches the 'words' table, not 'word_forms'.
     */
    override suspend fun updateWord(word: WordDomain) {
        wordDao.insertWord(word.toWordEntity())
    }

    /**
     * Heavy update: Merges new forms with existing ones in the DB.
     * Use this when parsing text or importing words.
     */
    override suspend fun syncWordWithDatabase(word: WordDomain) {
        val existingRelation = if (word.id != 0) {
            wordDao.getWordWithFormsById(word.id)
        } else {
            wordDao.getWordWithFormsByLemma(word.lemma)
        }

        if (existingRelation != null) {
            // If word exists, merge its forms with the new ones
            val updatedDomain = existingRelation.toDomain().addForms(word.forms)
            wordDao.insertWordWithForms(updatedDomain.toWordEntity(), updatedDomain.forms)
        } else {
            // If it's a completely new word
            wordDao.insertWordWithForms(word.toWordEntity(), word.forms)
        }
    }
    override suspend fun changeState(wordId: Int, state: WordState) {
        val word = wordDao.getWordWithFormsById(wordId)
        word?.let {
            val updatedWord = it.toDomain().withState(state)
            wordDao.insertWord(updatedWord.toWordEntity())
        }
    }

    override suspend fun addWords(words: List<WordDomain>) {
        val databaseWordsMap = wordDao.getAllWordsList().associateBy { it.word.lemma }
        val processingMap = mutableMapOf<String, WordDomain>()

        words.forEach { newWord ->
            // Check if we already handled this lemma in this loop OR if it's in the DB
            val existingEntry = processingMap[newWord.lemma]
                ?: databaseWordsMap[newWord.lemma]?.toDomain()

            if (existingEntry != null) {
                // MERGE LOGIC: Add forms AND sum the counts
                processingMap[newWord.lemma] = existingEntry
                    .addForms(newWord.forms)
            } else {
                // New word entirely
                processingMap[newWord.lemma] = newWord
            }
        }

        // Save to DB
        processingMap.values.forEach { domain ->
            wordDao.insertWordWithForms(domain.toWordEntity(), domain.forms)
        }
    }

    // --- Metadata & Deletion ---

    override suspend fun getAllLemmasAndForms(): List<String> {
        val data = wordDao.getAllWordsList()
        return data.flatMap { item -> listOf(item.word.lemma) + item.forms.map { it.form } }
    }

    override suspend fun getExistingWords(words: List<String>): Set<String> {
        val allData = wordDao.getAllWordsList()
        val lemmas = allData.map { it.word.lemma }.filter { it in words }
        val forms = allData.flatMap { it.forms.map { f -> f.form } }.filter { it in words }
        return (lemmas + forms).toSet()
    }

    override suspend fun deleteWord(word: WordDomain) = wordDao.deleteWord(word.toWordEntity())
    override suspend fun deleteById(id: Int) = wordDao.deleteById(id)
    override suspend fun deleteAllWords(): Int = wordDao.deleteAll()
}