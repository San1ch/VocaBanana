package com.san1ch.vocabanana.core.essentials.database.repositories

import com.san1ch.vocabanana.core.essentials.database.model.LexiconDto

interface LexiconRepository {
    suspend fun getExistingWords(words: List<String>): List<String>
    suspend fun getWordsFromWords(words: List<String>): List<LexiconDto>
}