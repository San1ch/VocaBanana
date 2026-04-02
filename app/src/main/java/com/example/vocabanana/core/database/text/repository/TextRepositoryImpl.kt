package com.example.vocabanana.core.database.text.repository

import com.example.vocabanana.core.database.text.local.TextDao
import com.example.vocabanana.core.database.text.local.TextEntity
import com.example.vocabanana.feature.text.domain.TextDomain
import com.example.vocabanana.feature.text.mapper.toDomainUnsafe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TextRepositoryImpl @Inject constructor(
    private val dao: TextDao
) : TextRepository {

    override fun getTexts(): Flow<List<TextDomain>> =
        dao.getTexts().map { list -> list.map { it.toDomainUnsafe() } }

    override fun getTextById(id: Int): TextDomain = dao.getTextById(id).toDomainUnsafe()

    override fun insertText(text: TextDomain) =
        dao.insertText(
            TextEntity(
                text.id,
                text.name,
                text.content,
                text.lastScrollPosition,
                text.lastReadTime
            )
        )

    override suspend  fun deleteText(textId: Int) = dao.deleteTextById(textId)

    override suspend  fun updateProgress(id: Int, position: Float, time: Long) {
        dao.updateProgress(id, position, time)
    }


    override fun isTextNameUnique(name: String): Boolean {
        return !dao.isNameUnique(name)
    }

}