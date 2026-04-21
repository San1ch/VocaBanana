package com.example.vocabanana.feature.database.text.repository

import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.io.storage.FileStorage
import com.example.vocabanana.feature.database.text.local.TextDao
import com.example.vocabanana.feature.database.text.local.TextEntity
import com.example.vocabanana.feature.text.domain.model.TextDomain
import com.example.vocabanana.feature.text.mapper.toDomainUnsafe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TextRepositoryImpl @Inject constructor(
    private val dao: TextDao,
    private val fileStorage: FileStorage
) : TextRepository {

    override fun getTexts(): Flow<List<TextDomain>> =
        dao.getTexts().map { list ->
            list.map { entity ->
                entity.toDomainUnsafe(content = "")
            }
        }


    override suspend fun getTextById(id: Int): TextDomain {
        val result = dao.getTextById(id)
        return result.toDomainUnsafe(fileStorage.loadText(result.contentPath))
    }


    override fun saveText(text: TextDomain) {
        val path = fileStorage.saveText(text.name, text.content)

        dao.insertText(
            TextEntity(
                text.id,
                text.name,
                path,
                text.info.lastScrollPosition,
                text.info.lastReadTime
            )
        )
    }

    override suspend  fun deleteText(textId: Int) = dao.deleteTextById(textId)

    override suspend  fun updateProgress(id: Int, position: Float, time: Long) {
        dao.updateProgress(id, position, time)
    }


    override fun isTextNameUnique(name: String): Boolean {
        return !dao.isNameUnique(name)
    }

}