package com.example.vocabanana.core.database.word.repository

import com.example.vocabanana.core.database.word.local.WordFormDao
import com.example.vocabanana.feature.word.domain.WordFormDomain
import com.example.vocabanana.feature.word.mapper.toDomain
import com.example.vocabanana.feature.word.mapper.toEntity
import javax.inject.Inject

class WordFormRepositoryImpl @Inject constructor(private val dao: WordFormDao) :
    WordFormRepository {
    override fun getWordFormsByWordId(wordId: Int): List<WordFormDomain> =
        dao.getWordFormsByWordId(wordId).map { it.toDomain(wordId) }

    override fun formExists(form: String): Boolean = dao.formExists(form)

    override fun insertWordForms(wordForms: List<WordFormDomain>) =
        dao.insertWordForms(wordForms.map { it.toEntity(it.wordId) })


}
