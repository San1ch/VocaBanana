package com.san1ch.vocabanana.core.android.database.text.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "texts")
data class TextEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,

    @ColumnInfo(name = "content_path")
    val contentPath: String,
)
