package com.san1ch.vocabanana.core.android.database.text.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.san1ch.vocabanana.core.essentials.model.text.TextWordCount

@Entity(
    tableName = "text_word_counts",
    primaryKeys = ["textId", "wordId"],
    foreignKeys = [
        ForeignKey(
            entity = TextEntity::class,
            parentColumns = ["id"],
            childColumns = ["textId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["textId"])]
)
data class TextWordCountEntity(
    val textId: Int,
    val wordId: Int,
    val count: Int
)

fun TextWordCount.toEntity() = TextWordCountEntity(textId, wordId, count)

fun TextWordCountEntity.toDomain() = TextWordCount(textId, wordId, count)
