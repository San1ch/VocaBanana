package com.san1ch.vocabanana.core.android.database

import androidx.room.TypeConverter
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.model.word.toWordState

class Converters {
    @TypeConverter
    fun fromWordStateSet(value: Set<WordState>): String = value.joinToString(",") { it.value.toString() }

    @TypeConverter
    fun toWordStateSet(value: String): Set<WordState> = if (value.isEmpty()) {
        emptySet()
    } else {
        value.split(",").map { it.toInt().toWordState() }.toSet()
    }
}
