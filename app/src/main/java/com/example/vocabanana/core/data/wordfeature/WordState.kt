package com.example.vocabanana.core.data.wordfeature


data class WordState(
    val wordId: Int,
    val state: WordStateParams
)

enum class WordStateParams {
    NEW,
    NOT_LEARNED,
    LEARNING,
    LEARNED
}