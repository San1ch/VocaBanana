package com.san1ch.vocabanana.core.android.database.text

import com.san1ch.vocabanana.core.essentials.model.text.TextInfo

data class TextMetadataProjection(
    val id: Int,
    val name: String,
)

fun TextMetadataProjection.toDomain(): TextInfo = TextInfo(
    id = id,
    name = name,
)

fun TextInfo.toEntity(): TextMetadataProjection = TextMetadataProjection(
    id = id,
    name = name,
)
