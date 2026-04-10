package com.example.vocabanana.feature.database.language.lexicon

interface LexiconRepository {
    suspend fun getExistingWords(words: List<String>): List<String>
}