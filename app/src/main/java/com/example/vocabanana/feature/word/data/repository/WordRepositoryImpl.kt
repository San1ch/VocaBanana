package com.example.vocabanana.feature.word.data.repository

import com.example.vocabanana.feature.word.domain.WordDomain
import com.example.vocabanana.feature.word.data.local.WordDao
import com.example.vocabanana.feature.word.data.local.WordEntity
import com.example.vocabanana.feature.word.data.local.WordFormDao
import com.example.vocabanana.feature.word.data.local.WordWithFormsEntity
import com.example.vocabanana.feature.word.data.toInt
import com.example.vocabanana.feature.word.data.toWordState
import com.example.vocabanana.feature.word.domain.PartOfSpeech
import com.example.vocabanana.feature.word.domain.WordRepository
import com.example.vocabanana.feature.word.domain.toInt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordRepositoryRoomImpl @Inject constructor(
    private val wordDao: WordDao,
    private val formDao: WordFormDao
) : WordRepository {

    override fun getAllWords(): Flow<List<WordDomain>> =
        wordDao.getAllWords().map { list ->
            list.map { it.toDomain() }
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
}

fun WordDomain.toWordEntity() = WordEntity(
    id = id,
    lemma = lemma,
    state = state.toInt(),
    whenAdded = whenAdded,
    partOfSpeech = partOfSpeech.toInt(),
)
fun WordWithFormsEntity.toDomain() = WordDomain.createUnsafe(
    id = word.id,
    lemma = word.lemma,
    whenAdded = word.whenAdded,
    state = word.state.toWordState(),
    partOfSpeech = PartOfSpeech.entries[word.partOfSpeech],
    forms = forms.map { it.toDomain(word.id) }
)