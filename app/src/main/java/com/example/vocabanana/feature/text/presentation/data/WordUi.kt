package com.example.vocabanana.feature.text.presentation.data

data class WordUi(
    val id: Int,
    val word: String,
    val whenAdded: Long,
    val partOfSpeech: String,
    val forms: List<WordFormUi>
)

data class WordFormUi(
    val form: String,
    val partOfSpeech: String
)
