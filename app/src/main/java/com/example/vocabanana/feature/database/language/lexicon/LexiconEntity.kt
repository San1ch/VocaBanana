package com.example.vocabanana.feature.database.language.lexicon

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "lexicon-en", indices = [Index(value = ["word"], name = "idx_word")])
data class LexiconEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "word")
    val word: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "definitions")
    val definition: String
)
