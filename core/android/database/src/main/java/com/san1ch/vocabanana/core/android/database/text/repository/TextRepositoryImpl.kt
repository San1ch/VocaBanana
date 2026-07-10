package com.san1ch.vocabanana.core.android.database.text.repository

import com.san1ch.vocabanana.core.essentials.repositories.FileStorage
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.android.database.text.local.TextDao
import com.san1ch.vocabanana.core.android.database.text.local.TextEntity
import com.san1ch.vocabanana.core.android.database.text.toDomainUnsafe
import com.san1ch.vocabanana.core.android.database.text.local.TextWordCountDao
import com.san1ch.vocabanana.core.android.database.text.local.toEntity
import com.san1ch.vocabanana.core.essentials.model.text.TextDomain
import com.san1ch.vocabanana.core.essentials.model.text.TextWordCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class TextRepositoryImpl @Inject constructor(
    private val textDao: TextDao,
    private val fileStorage: FileStorage,
    private val textWordCountDao: TextWordCountDao
) : TextRepository {

    override fun getTexts(): Flow<List<TextDomain>> =
        textDao.getTexts().map { list ->
            list.map { entity ->
                entity.toDomainUnsafe(content = "")
            }
        }
    override suspend fun getTextById(id: Int): Result<TextDomain> {
        val text = when(val result = textDao.getTextById(id)){
            null -> return Result.failure(Exception("Text not found"))
            else -> result.toDomainUnsafe(fileStorage.loadText(result.contentPath))
        }
        return Result.success(text)
    }


    override fun saveText(text: TextDomain) {
        val path = fileStorage.saveText(text.name, text.content)

        textDao.insertText(
            TextEntity(
                text.id,
                text.name,
                path,
                text.info.lastScrollPosition,
                text.info.lastReadTime
            )
        )
    }

    override suspend  fun deleteText(textId: Int) = textDao.deleteTextById(textId)

    override suspend  fun updateProgress(id: Int, position: Float, time: Long) {
        textDao.updateProgress(id, position, time)
    }


    override fun isTextNameUnique(name: String): Boolean {
        return !textDao.isNameUnique(name)
    }

    override suspend fun saveTextWordCounts(textWordCounts: List<TextWordCount>) {
        if (textWordCounts.isEmpty()) return
        val entities = textWordCounts.map { it.toEntity() }
        textWordCountDao.insertWordCounts(entities)
    }

    override suspend fun getTextWordCounts(wordIds: List<Int>): Map<Int, Int> {
        val entities = textWordCountDao.getWordCountsByWordList(wordIds)
        return entities
            .groupBy { it.wordId }
            .mapValues { entry ->
                entry.value.sumOf { it.count }
            }
    }

}