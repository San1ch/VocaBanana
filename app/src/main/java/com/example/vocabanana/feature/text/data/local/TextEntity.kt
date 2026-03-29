package com.example.vocabanana.feature.text.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vocabanana.feature.text.data.TextDomain
import com.example.vocabanana.core.data.fold

@Entity(tableName = "texts")
data class TextEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "last_scroll_position")
    val lastScrollPosition: Float,

    @ColumnInfo(name = "last_read_time")
    val lastReadTime: Long
)

fun TextEntity.toDomain(): TextDomain =
    TextDomain.create(id, name, content, lastScrollPosition, lastReadTime).fold(
        onSuccess = { it },
        onError = {
            println("Error creating TextDomain: $it")
            throw RuntimeException("Error creating TextDomain: $it")
        }
    )

fun TextEntity.toDomainUnsafe(): TextDomain =
    TextDomain.unsafeCreate(id, name, content, lastScrollPosition, lastReadTime)