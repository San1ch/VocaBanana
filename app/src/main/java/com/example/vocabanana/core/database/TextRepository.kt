package com.example.vocabanana.core.database

import com.example.vocabanana.feature.text.domain.model.TextDomain
import kotlinx.coroutines.flow.Flow

interface TextRepository {

    fun getTexts(): Flow<List<TextDomain>>
    fun getTextById(id: Int): TextDomain
    fun saveText(text: TextDomain)
    suspend fun deleteText(textId: Int)
    suspend fun updateProgress(id: Int, position: Float, time: Long)
    fun isTextNameUnique(name: String): Boolean


}