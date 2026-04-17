package com.example.vocabanana.feature.database.word.repository

import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.feature.database.word.local.WordDao
import com.example.vocabanana.feature.word.domain.model.WordDomain
import com.example.vocabanana.feature.word.domain.model.WordState
import com.example.vocabanana.feature.word.mapper.toDomain
import com.example.vocabanana.feature.word.mapper.toWordEntity
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

    override suspend fun getWordById(id: Int): WordDomain? =
        wordDao.getWordWithFormsById(id)?.toDomain()

    override suspend fun getWordByWord(word: String): WordDomain? =
        wordDao.getWordByAnyForm(word.trim().lowercase())?.toDomain()

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

    override suspend fun addWords(words: List<WordDomain>) {
        // Fetch existing data once using suspend method (much faster than .first())
        val databaseWordsMap = wordDao.getAllWordsList().associateBy { it.word.lemma }
        val processingMap = mutableMapOf<String, WordDomain>()

        words.forEach { newWord ->
            val existingEntry = processingMap[newWord.lemma]
                ?: databaseWordsMap[newWord.lemma]?.toDomain()

            processingMap[newWord.lemma] = existingEntry?.addForms(newWord.forms) ?: newWord
        }

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
    override suspend fun deleteAll(): Int = wordDao.deleteAll()
}