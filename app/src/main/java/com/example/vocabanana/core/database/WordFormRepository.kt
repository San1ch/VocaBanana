package com.example.vocabanana.core.database

import com.example.vocabanana.feature.word.domain.model.WordFormDomain

interface WordFormRepository {
    fun getWordFormsByWordId(wordId: Int): List<WordFormDomain>
    fun formExists(form: String): Boolean
    fun insertWordForms(wordForms: List<WordFormDomain>)
}