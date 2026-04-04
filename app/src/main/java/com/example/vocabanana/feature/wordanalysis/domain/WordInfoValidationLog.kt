package com.example.vocabanana.feature.wordanalysis.domain

data class WordInfoValidationLog(
    val validationName: String,
    val inputWords: List<String>,
    val outputWords: List<String>,
    val prompt: String,
    val response: String,
    val timestamp: Long = System.currentTimeMillis()
)