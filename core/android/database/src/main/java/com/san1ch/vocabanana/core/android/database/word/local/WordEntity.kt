package com.san1ch.vocabanana.core.android.database.word.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    val lemma: String,
    val state: Int,
    @ColumnInfo(name = "when_added") val whenAdded: Long,
    val partOfSpeech: Int,
    val definition: String,
)

@Entity(
    tableName = "word_forms",
    primaryKeys = ["wordId", "form"],
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class WordFormEntity(
    val wordId: Int,
    val form: String,
)

data class WordWithForms(
    @Embedded val word: WordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "wordId",
    )
    val forms: List<WordFormEntity>,
)
