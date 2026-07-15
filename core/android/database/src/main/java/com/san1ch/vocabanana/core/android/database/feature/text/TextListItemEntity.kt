package com.san1ch.vocabanana.core.android.database.feature.text

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.san1ch.vocabanana.core.android.database.text.local.TextEntity
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.feature.text.domain.model.ReadingState

@Entity(
    tableName = "reading_states",
    foreignKeys = [
        ForeignKey(
            entity = TextEntity::class,
            parentColumns = ["id"],
            childColumns = ["textId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReadingStateEntity(
    @PrimaryKey val textId: Int,

    val lastReadTime: Long,
    val lastScrollPosition: Float,

    val fontSize: Int,
    val lineSpacing: Int,
    val paragraphSpacing: Int,
    val horizontalPadding: Int,

    val activeWordStates: Set<WordState>
)



fun ReadingStateEntity.toDomain() = ReadingState(
    textId = textId,
    lastReadTime = lastReadTime,
    lastScrollPosition = lastScrollPosition,
    fontSize = fontSize,
    lineSpacing = lineSpacing,
    paragraphSpacing = paragraphSpacing,
    horizontalPadding = horizontalPadding,
    activeWordStates = activeWordStates
)


fun ReadingState.toEntity() = ReadingStateEntity(
    textId = textId,
    lastReadTime = lastReadTime ?: 0L,
    lastScrollPosition = lastScrollPosition ?: 0f,
    fontSize = fontSize,
    lineSpacing = lineSpacing,
    paragraphSpacing = paragraphSpacing,
    horizontalPadding = horizontalPadding,
    activeWordStates = activeWordStates
)