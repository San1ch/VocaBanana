package com.example.vocabanana.feature.word.data

import com.example.vocabanana.feature.word.data.WordState.entries

enum class WordState(val value: Int) {
    NEW(0),
    NOT_KNOWN(1),
    LEARNING(2),
    KNOWN(3),
    IGNORED(4);
}


fun WordState.toInt() = value
fun Int.toWordState() = entries.first { it.value == this }