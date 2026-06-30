package com.san1ch.vocabanana.core.essentials.database.model.word

import kotlin.collections.first


enum class WordState(val value: Int) {
    NEW(0),
    LEARNING(1),
    NOT_KNOWN(2),
    KNOWN(3),
    IGNORED(4);
}

fun WordState.toInt() = value
fun Int.toWordState() = WordState.entries.first { it.value == this }