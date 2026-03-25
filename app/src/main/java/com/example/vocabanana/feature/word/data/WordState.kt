package com.example.vocabanana.feature.word.data

enum class WordState(val value: Int) {
    NEW(0),
    NOT_KNOWN(1),
    LEARNING(2),
    KNOWN(3),
    IGNORED(4);
    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
        fun toInt(value: WordState) = value.value
    }
}


