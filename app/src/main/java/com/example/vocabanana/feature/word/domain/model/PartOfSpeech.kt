package com.example.vocabanana.feature.word.domain.model

enum class PartOfSpeech(val value: Int, val shortName: String) {
    NOUN(0, "noun"),
    VERB(1, "verb"),
    ADJECTIVE(2, "adj"),
    ADVERB(3, "adv"),
    PRONOUN(4, "pron"),
    DETERMINER(5, "det"),
    NUMERAL(6, "num"),
    PREPOSITION(7, "prep"),
    CONJUNCTION(8, "conj"),
    INTERJECTION(9, "intj"),
    PARTICLE(10, "part"),
    AUXILIARY(11, "aux"),
    MODAL(12, "modal"),
    ARTICLE(13, "art"),
    UNKNOWN(14, "other");

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
