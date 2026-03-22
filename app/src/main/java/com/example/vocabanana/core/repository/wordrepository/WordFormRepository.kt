package com.example.vocabanana.core.repository.wordrepository

import com.example.vocabanana.core.repository.wordrepository.room.form.WordFormsEntity

interface WordFormRepository {
    fun getWordFormsByWordId(wordId: Int): List<WordFormsEntity>
    fun getWordFormsByForm(form: String): List<WordFormsEntity>
    fun insertWordForms(wordForms: List<WordFormsEntity>)
}