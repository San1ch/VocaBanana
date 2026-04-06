package com.example.vocabanana.feature.database.word.local

import androidx.room.Embedded
import androidx.room.Relation

data class WordWithFormsEntity(
    @Embedded val word: WordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "wordId"
    )
    val forms: List<WordFormEntity>
)