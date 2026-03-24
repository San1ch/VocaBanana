package com.example.vocabanana.feature.word.data.repository

import com.example.vocabanana.feature.word.data.local.WordFormDao
import com.example.vocabanana.feature.word.data.local.WordFormsEntity
import com.example.vocabanana.feature.word.domain.WordFormRepository
import javax.inject.Inject

class WordFormRepositoryImpl @Inject constructor(private val dao: WordFormDao) :
    WordFormRepository {
    override fun getWordFormsByWordId(wordId: Int): List<WordFormsEntity> =
        dao.getWordFormsByWordId(wordId)

    override fun getWordFormsByForm(form: String): List<WordFormsEntity> =
        dao.getWordFormsByForm(form)

    override fun insertWordForms(wordForms: List<WordFormsEntity>) =
        dao.insertWordForms(wordForms)


}