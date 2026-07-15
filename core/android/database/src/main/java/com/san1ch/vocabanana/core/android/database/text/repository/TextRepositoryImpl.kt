package com.san1ch.vocabanana.core.android.database.text.repository

import com.san1ch.vocabanana.core.android.database.text.local.TextDao
import com.san1ch.vocabanana.core.android.database.text.local.TextEntity
import com.san1ch.vocabanana.core.android.database.text.local.TextWordCountDao
import com.san1ch.vocabanana.core.android.database.text.local.toEntity
import com.san1ch.vocabanana.core.android.database.text.toDomain
import com.san1ch.vocabanana.core.android.database.text.toDomainUnsafe
import com.san1ch.vocabanana.core.essentials.extentionfuncs.toParagraphs
import com.san1ch.vocabanana.core.essentials.model.text.TextDomain
import com.san1ch.vocabanana.core.essentials.model.text.TextInfo
import com.san1ch.vocabanana.core.essentials.model.text.TextWordCount
import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import com.san1ch.vocabanana.core.essentials.repositories.FileStorage
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class TextRepositoryImpl @Inject constructor(
    private val textDao: TextDao,
    private val fileStorage: FileStorage,
    private val textWordCountDao: TextWordCountDao,
) : TextRepository {
    override fun getWordIdsByTextIds(textIds: FilterType<Int>): Flow<List<Int>> = when (textIds) {
        is FilterType.All ->
            textWordCountDao.getWordIdsByTextIds(
                emptyList(),
                filter = false,
                exclude = false,
            )

        is FilterType.Include ->
            textWordCountDao.getWordIdsByTextIds(
                textIds.items,
                filter = true,
                exclude = false,
            )

        is FilterType.Exclude ->
            textWordCountDao.getWordIdsByTextIds(
                textIds.items,
                filter = true,
                exclude = true,
            )
    }

    override fun getContentById(id: Int): Flow<List<String>> {
        return textDao.getTextByIdFlow(id).map { entity ->
            entity?.let {
                fileStorage.loadText(it.contentPath).toParagraphs()
            } ?: emptyList()
        }
    }

    override fun getTextsMetadata(): Flow<List<TextInfo>> = textDao.getTexts().map { list ->
        list.map { entity -> TextInfo(entity.id, entity.name) }
    }

    override fun getTextMetadataByIdFlow(id: Int): Flow<TextInfo?> {
        return textDao.getTextByIdFlow(id).map { entity ->
            entity?.let { TextInfo(it.id, it.name) }
        }
    }

    override fun getTexts(): Flow<List<TextDomain>> = textDao.getTexts().map { list ->
        list.map { entity ->
            entity.toDomainUnsafe(content = fileStorage.loadText(entity.contentPath))
        }
    }

    override suspend fun getTextsByIds(ids: List<Int>): List<TextDomain> {
        val entities = textDao.getTextsByIds(ids)
        return entities.map { entity ->
            entity.toDomainUnsafe(fileStorage.loadText(entity.contentPath))
        }
    }

    override fun saveTexts(texts: List<TextDomain>) {
        val entities = texts.map { text ->
            val path = fileStorage.saveText(text.name, text.content)
            TextEntity(text.id, text.name, path)
        }
        textDao.insertTexts(entities)
    }

    override suspend fun deleteTexts(textIds: List<Int>) {
        // Optional: fetch entities first to delete files
        val entities = textDao.getTextsByIds(textIds)
        entities.forEach { fileStorage.deleteText(it.contentPath) }

        textDao.deleteTextsByIds(textIds)
    }

    override fun isTextNameUnique(name: String): Boolean = !textDao.isNameUnique(name)

    override suspend fun saveTextWordCounts(textWordCounts: List<TextWordCount>) {
        if (textWordCounts.isEmpty()) return
        val entities = textWordCounts.map { it.toEntity() }
        textWordCountDao.insertWordCounts(entities)
    }

    override suspend fun getTextWordCounts(wordIds: List<Int>): Map<Int, Int> {
        if (wordIds.isEmpty()) return emptyMap()

        val entities = textWordCountDao.getWordCountsByWordList(wordIds)

        return entities
            .groupBy { it.wordId }
            .mapValues { entry ->
                entry.value.sumOf { it.count }
            }
    }
}
