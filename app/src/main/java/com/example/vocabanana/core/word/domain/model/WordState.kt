package com.example.vocabanana.core.word.domain.model

import kotlin.collections.first


fun WordState.toInt() = value
fun Int.toWordState() = WordState.entries.first { it.value == this }