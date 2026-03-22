package com.example.vocabanana.core.repository.wordrepository.room.form

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.vocabanana.core.repository.wordrepository.room.word.WordEntity

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
data class WordFormsEntity(
    @PrimaryKey val id: Int,
    val wordId: Int,
    val form: String
)