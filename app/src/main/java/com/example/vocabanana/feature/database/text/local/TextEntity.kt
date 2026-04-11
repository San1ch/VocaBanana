package com.example.vocabanana.feature.database.text.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "texts")
data class TextEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,

    @ColumnInfo(name = "content_path")
    val contentPath: String,

    @ColumnInfo(name = "last_scroll_position")
    val lastScrollPosition: Float,

    @ColumnInfo(name = "last_read_time")
    val lastReadTime: Long
)

