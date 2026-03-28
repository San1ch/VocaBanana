package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.feature.text.data.TextDomain
import kotlinx.coroutines.flow.Flow

interface TextRepository {

    fun getTexts(): Flow<List<TextDomain>>
     fun getTextById(id: Int): TextDomain
     fun insertText(text: TextDomain)
     fun deleteText(text: TextDomain)


     fun isTextNameUnique(name: String): Boolean


}