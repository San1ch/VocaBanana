package com.example.vocabanana.feature.word.data.repository

import com.example.vocabanana.feature.word.data.local.WordFormDao
import com.example.vocabanana.feature.word.data.local.WordFormEntity
import com.example.vocabanana.feature.word.domain.PartOfSpeech
import com.example.vocabanana.feature.word.domain.WordFormDomain
import com.example.vocabanana.feature.word.domain.WordFormRepository
import com.example.vocabanana.feature.word.domain.toInt
import javax.inject.Inject

class WordFormRepositoryImpl @Inject constructor(private val dao: WordFormDao) :
    WordFormRepository {
    override fun getWordFormsByWordId(wordId: Int): List<WordFormDomain> =
        dao.getWordFormsByWordId(wordId).map { it.toDomain(wordId) }

    override fun formExists(form: String): Boolean = dao.formExists(form)

    override fun insertWordForms(wordForms: List<WordFormDomain>) =
        dao.insertWordForms(wordForms.map { it.toEntity(it.wordId) })


}

fun WordFormDomain.toEntity(wordId: Int) = WordFormEntity(
    id = id,
    wordId = wordId,
    form = form,
    partOfSpeech = partOfSpeech.toInt()
)

fun WordFormEntity.toDomain(wordId: Int) = WordFormDomain.createUnsafe(
    id = id,
    wordId = wordId,
    form = form,
    partOfSpeech = PartOfSpeech.entries[partOfSpeech]
)