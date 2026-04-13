package com.example.vocabanana.feature.database.word.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.json.Json


@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lemma: String,
    val state: Int,
    @ColumnInfo(name = "when_added") val whenAdded: Long,
    val forms: List<String>,
    val partOfSpeech: Int,
)

class WordConverters {
    @TypeConverter
    fun fromList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        return Json.decodeFromString(value)
    }

}