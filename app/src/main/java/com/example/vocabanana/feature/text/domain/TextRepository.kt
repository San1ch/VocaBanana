package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.feature.text.data.TextDomain

interface TextRepository {

    fun getTexts(): List<TextDomain>
    fun getTextById(id: Int): TextDomain
    fun insertText(text: TextDomain)
    fun deleteText(text: TextDomain)
}