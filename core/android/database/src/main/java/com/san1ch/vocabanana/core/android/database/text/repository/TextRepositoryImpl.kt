package com.san1ch.vocabanana.core.android.database.text.repository

import com.san1ch.vocabanana.core.essentials.repositories.FileStorage
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.android.database.text.local.TextDao
import com.san1ch.vocabanana.core.android.database.text.local.TextEntity
import com.san1ch.vocabanana.core.android.database.text.toDomainUnsafe
import com.san1ch.vocabanana.core.essentials.model.text.TextDomain
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
    override suspend fun getTextById(id: Int): Result<TextDomain> {
        val text = when(val result = dao.getTextById(id)){
            null -> return Result.failure(Exception("Text not found"))
            else -> result.toDomainUnsafe(fileStorage.loadText(result.contentPath))
        }
        return Result.success(text)
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