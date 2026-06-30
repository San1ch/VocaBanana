package com.san1ch.vocabanana.core.essentials.database.model.word

object PartOfSpeechShorNames{
    const val NOUN = "noun"
    const val VERB = "verb"
    const val ADJECTIVE = "adj"
    const val ADVERB = "adv"
    const val UNKNOWN = "other"
}

enum class PartOfSpeech(val value: Int, val shortName: String) {
    NOUN(0, PartOfSpeechShorNames.NOUN),
    VERB(1, PartOfSpeechShorNames.VERB),
    ADJECTIVE(2, PartOfSpeechShorNames.ADJECTIVE),
    ADVERB(3, PartOfSpeechShorNames.ADVERB),
    UNKNOWN(99, PartOfSpeechShorNames.UNKNOWN);


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
fun String.toPartOfSpeech() = PartOfSpeech.fromShortName(this)
