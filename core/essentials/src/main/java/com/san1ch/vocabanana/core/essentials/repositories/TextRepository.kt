package com.san1ch.vocabanana.core.essentials.repositories

import com.san1ch.vocabanana.core.essentials.model.text.TextDomain
import com.san1ch.vocabanana.core.essentials.model.text.TextInfo
import com.san1ch.vocabanana.core.essentials.model.text.TextWordCount
import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import kotlinx.coroutines.flow.Flow

interface TextRepository {
    fun getWordIdsByTextIds(textIds: FilterType<Int>): Flow<List<Int>>
    fun getContentById(id: Int): Flow<List<String>>
    fun getTexts(): Flow<List<TextDomain>>
    suspend fun getTextsByIds(ids: List<Int>): List<TextDomain>
    fun getTextsMetadata(): Flow<List<TextInfo>>
    fun getTextMetadataByIdFlow(id: Int): Flow<TextInfo?>
    fun saveTexts(texts: List<TextDomain>)
    suspend fun deleteTexts(textIds: List<Int>)

    fun isTextNameUnique(name: String): Boolean

    suspend fun saveTextWordCounts(textWordCounts: List<TextWordCount>)

    suspend fun getTextWordCounts(wordIds: List<Int>): Map<Int, Int>
}
