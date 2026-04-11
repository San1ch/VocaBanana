package com.example.vocabanana.feature.text.mapper

import com.example.vocabanana.core.domain.model.fold
import com.example.vocabanana.feature.database.text.local.TextEntity
import com.example.vocabanana.feature.text.domain.model.TextDomain

fun TextEntity.toDomain(): TextDomain =
    TextDomain.create(id, name, contentPath, lastScrollPosition, lastReadTime).fold(
        onSuccess = { it },
        onError = {
            println("Error creating TextDomain: $it")
            throw RuntimeException("Error creating TextDomain: $it")
        }
    )

fun TextEntity.toDomainUnsafe(content: String): TextDomain =
    TextDomain.unsafeCreate(id, name, content, lastScrollPosition, lastReadTime)