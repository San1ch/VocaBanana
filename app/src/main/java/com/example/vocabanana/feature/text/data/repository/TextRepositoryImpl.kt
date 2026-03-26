package com.example.vocabanana.feature.text.data.repository

import com.example.vocabanana.feature.text.data.TextDomain
import com.example.vocabanana.feature.text.data.local.TextDao
import com.example.vocabanana.feature.text.data.local.TextEntity
import com.example.vocabanana.feature.text.data.local.toDomainUnsafe
import com.example.vocabanana.feature.text.domain.TextRepository
import javax.inject.Inject

class TextRepositoryImpl @Inject constructor(
    private val dao: TextDao
) : TextRepository {

    override fun getTexts(): List<TextDomain> = dao.getTexts().map { it.toDomainUnsafe() }

    override fun getTextById(id: Int): TextDomain = dao.getTextById(id).toDomainUnsafe()

    override fun insertText(text: TextDomain) =
        dao.insertText(TextEntity(text.id, text.name, text.content))

    override fun deleteText(text: TextDomain) =
        dao.deleteText(TextEntity(text.id, text.name, text.content))

}