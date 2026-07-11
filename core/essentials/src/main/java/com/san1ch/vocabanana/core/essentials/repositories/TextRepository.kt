package com.san1ch.vocabanana.core.essentials.repositories

import com.san1ch.vocabanana.core.essentials.model.text.TextDomain
import com.san1ch.vocabanana.core.essentials.model.text.TextWordCount
import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import kotlinx.coroutines.flow.Flow

interface TextRepository {
    fun getWordIdsByTextIds(textIds: FilterType<Int>): Flow<List<Int>>

    fun getTexts(): Flow<List<TextDomain>>
    suspend fun getTextById(id: Int): Result<TextDomain>
    fun saveText(text: TextDomain)
    suspend fun deleteText(textId: Int)
    suspend fun updateProgress(id: Int, position: Float, time: Long)
    fun isTextNameUnique(name: String): Boolean
    suspend fun saveTextWordCounts(textWordCounts: List<TextWordCount>)
    suspend fun getTextWordCounts(wordIds: List<Int>): Map<Int, Int>
}