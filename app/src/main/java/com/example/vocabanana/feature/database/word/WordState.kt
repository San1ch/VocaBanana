package com.example.vocabanana.feature.database.word

import com.example.vocabanana.feature.word.domain.model.WordState
import kotlin.collections.first


fun WordState.toInt() = value
fun Int.toWordState() = WordState.entries.first { it.value == this }