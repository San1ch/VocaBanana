package com.example.vocabanana.core.database.word

import kotlin.collections.first

enum class WordState(val value: Int) {
    NEW(0),
    NOT_KNOWN(1),
    LEARNING(2),
    KNOWN(3),
    IGNORED(4);
}


fun WordState.toInt() = value
fun Int.toWordState() = WordState.entries.first { it.value == this }