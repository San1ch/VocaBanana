package com.san1ch.vocabanana.core.essentials.repositories

import com.san1ch.vocabanana.core.essentials.model.LexiconDto

interface LexiconRepository {
    suspend fun getExistingWords(words: List<String>): List<String>
    suspend fun getWordsFromWords(words: List<String>): List<LexiconDto>
}