package com.example.vocabanana.core.database

import com.example.vocabanana.core.database.model.WordLemmaDto

interface LemmatizationRepository {

    suspend fun getLemmasForWords(words: List<String>): List<String>

    suspend fun getLemmaForWord(word: String): String?

    suspend fun getWordLemmaPairs(words: List<String>): List<WordLemmaDto>

    suspend fun findExistingLemmas(words: List<String>): List<String>
    suspend fun findExistingWords(words: List<String>): List<String>

}