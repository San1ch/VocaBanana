package com.example.vocabanana.core.database.word.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "word_form",
    foreignKeys = [ForeignKey(
        entity = WordEntity::class,
        parentColumns = ["id"],
        childColumns = ["wordId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(
        value = ["wordId","form"],
    )]
)
data class WordFormEntity(
    @PrimaryKey val id: Int,
    val wordId: Int,
    val form: String,
    val partOfSpeech: Int,
)