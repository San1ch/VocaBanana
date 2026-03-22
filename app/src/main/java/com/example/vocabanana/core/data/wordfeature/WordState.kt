package com.example.vocabanana.wordfeature


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