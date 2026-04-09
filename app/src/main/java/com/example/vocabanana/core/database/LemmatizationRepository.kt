package com.example.vocabanana.core.database

import com.example.vocabanana.feature.database.lemmatizationtablet.LemmaEntity
import com.example.vocabanana.feature.database.lemmatizationtablet.WordLemmaDto

interface LemmatizationRepository {

    suspend fun getLemmasForWords(words: List<String>): List<String>

    suspend fun getWordLemmaPairs(words: List<String>): List<WordLemmaDto>

    suspend fun findExistingLemmas(words: List<String>): List<String>
}