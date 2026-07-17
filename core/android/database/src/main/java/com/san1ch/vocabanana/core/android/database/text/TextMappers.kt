package com.san1ch.vocabanana.core.android.database.text

import com.san1ch.vocabanana.core.android.database.text.local.TextEntity
import com.san1ch.vocabanana.core.essentials.model.fold
import com.san1ch.vocabanana.core.essentials.model.text.TextDomain

fun TextEntity.toDomain(): TextDomain = TextDomain.create(id, name, contentPath).fold(
    onSuccess = { it },
    onError = {
        println("Error creating TextDomain: $it")
        throw RuntimeException("Error creating TextDomain: $it")
    },
)

fun TextEntity.toDomainUnsafe(content: String): TextDomain = TextDomain.unsafeCreate(id, name, content)
