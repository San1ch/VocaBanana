package com.example.vocabanana.feature.database.lemmatizationtablet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lemmatization-en",
    indices = [Index(value = ["word"]), Index(value = ["lemma"])]
)
data class LemmaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val lemma: String
)