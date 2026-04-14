package com.example.vocabanana.feature.word.domain.model

enum class PartOfSpeech(val value: Int, val shortName: String) {
    NOUN(0, "noun"),
    VERB(1, "verb"),
    ADJECTIVE(2, "adj"),
    ADVERB(3, "adv"),
    UNKNOWN(99, "other");


    companion object {
        fun fromInt(value: Int): PartOfSpeech {
            return entries.find { it.value == value } ?: UNKNOWN
        }

        fun fromShortName(name: String): PartOfSpeech {
            return entries.find { it.shortName == name.lowercase() } ?: UNKNOWN
        }
    }
}

fun PartOfSpeech.toInt() = value
fun Int.toPartOfSpeech() = PartOfSpeech.fromInt(this)
