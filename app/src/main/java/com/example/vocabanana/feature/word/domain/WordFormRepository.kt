package com.example.vocabanana.feature.word.domain

interface WordFormRepository {
    fun getWordFormsByWordId(wordId: Int): List<WordFormDomain>
    fun formExists(form: String): Boolean
    fun insertWordForms(wordForms: List<WordFormDomain>)
}