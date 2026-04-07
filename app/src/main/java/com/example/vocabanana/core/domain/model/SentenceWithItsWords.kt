package com.example.vocabanana.core.domain.model

data class SentenceWithItsWords(
    val sentence: String,
    val words: List<String>
){
    fun wordsCount(): Int {
        return words.size
    }
}