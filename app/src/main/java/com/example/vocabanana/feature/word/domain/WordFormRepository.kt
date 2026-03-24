package com.example.vocabanana.feature.word.domain

import com.example.vocabanana.feature.word.data.local.WordFormsEntity

interface WordFormRepository {
    fun getWordFormsByWordId(wordId: Int): List<WordFormsEntity>
    fun getWordFormsByForm(form: String): List<WordFormsEntity>
    fun insertWordForms(wordForms: List<WordFormsEntity>)
}