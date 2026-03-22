package com.example.vocabanana.core.repository.wordrepository.room.form

import com.example.vocabanana.core.repository.wordrepository.WordFormRepository
import javax.inject.Inject

class WordFormRepositoryRoomImpl @Inject constructor(private val dao: WordFormDao) :
    WordFormRepository {
    override fun getWordFormsByWordId(wordId: Int): List<WordFormsEntity> =
        dao.getWordFormsByWordId(wordId)

    override fun getWordFormsByForm(form: String): List<WordFormsEntity> =
        dao.getWordFormsByForm(form)

    override fun insertWordForms(wordForms: List<WordFormsEntity>) =
        dao.insertWordForms(wordForms)


}