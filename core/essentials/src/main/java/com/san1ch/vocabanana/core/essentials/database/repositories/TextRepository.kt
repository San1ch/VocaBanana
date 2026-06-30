package com.san1ch.vocabanana.core.essentials.database.repositories

import com.san1ch.vocabanana.core.essentials.database.model.text.TextDomain
import kotlinx.coroutines.flow.Flow

interface TextRepository {

    fun getTexts(): Flow<List<TextDomain>>
    suspend fun getTextById(id: Int): Result<TextDomain>
    fun saveText(text: TextDomain)
    suspend fun deleteText(textId: Int)
    suspend fun updateProgress(id: Int, position: Float, time: Long)
    fun isTextNameUnique(name: String): Boolean
}