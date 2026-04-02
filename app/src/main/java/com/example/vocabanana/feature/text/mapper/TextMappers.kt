package com.example.vocabanana.feature.text.mapper

import com.example.vocabanana.core.data.fold
import com.example.vocabanana.core.database.text.local.TextEntity
import com.example.vocabanana.feature.text.domain.TextDomain

fun TextEntity.toDomain(): TextDomain =
    TextDomain.Companion.create(id, name, content, lastScrollPosition, lastReadTime).fold(
        onSuccess = { it },
        onError = {
            println("Error creating TextDomain: $it")
            throw RuntimeException("Error creating TextDomain: $it")
        }
    )

fun TextEntity.toDomainUnsafe(): TextDomain =
    TextDomain.Companion.unsafeCreate(id, name, content, lastScrollPosition, lastReadTime)