package com.example.vocabanana.core.database.word.repository

import com.example.vocabanana.feature.word.domain.WordFormDomain

interface WordFormRepository {
    fun getWordFormsByWordId(wordId: Int): List<WordFormDomain>
    fun formExists(form: String): Boolean
    fun insertWordForms(wordForms: List<WordFormDomain>)
}