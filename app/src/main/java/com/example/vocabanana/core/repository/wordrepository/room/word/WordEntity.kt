package com.example.vocabanana.core.repository.wordrepository.room.word

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "when_added") val whenAdded: Long,


)


