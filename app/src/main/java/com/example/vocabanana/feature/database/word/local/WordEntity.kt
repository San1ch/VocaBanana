package com.example.vocabanana.feature.database.word.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lemma: String,
    val state: Int,
    @ColumnInfo(name = "when_added") val whenAdded: Long,
    val partOfSpeech: Int,
)
