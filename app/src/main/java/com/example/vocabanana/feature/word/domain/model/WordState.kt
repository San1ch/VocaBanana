package com.example.vocabanana.feature.word.domain.model

import kotlin.collections.first


fun WordState.toInt() = value
fun Int.toWordState() = WordState.entries.first { it.value == this }