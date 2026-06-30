package com.san1ch.vocabanana.core.essentials.database.repositories

import com.san1ch.vocabanana.core.essentials.database.model.WordLemmaDto

interface LemmatizationRepository {

    suspend fun getLemmasForWords(words: List<String>): List<String>
    suspend fun getLemmaForWord(word: String): Result<String>
    suspend fun getWordLemmaPairs(words: List<String>): List<WordLemmaDto>
    suspend fun findExistingLemmas(words: List<String>): List<String>
    suspend fun findExistingWords(words: List<String>): List<String>
}