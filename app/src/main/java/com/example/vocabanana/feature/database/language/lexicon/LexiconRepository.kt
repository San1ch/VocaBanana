package com.example.vocabanana.feature.database.language.lexicon

import com.example.vocabanana.feature.word.domain.model.PartOfSpeech
import com.example.vocabanana.feature.word.domain.model.WordDomain

interface LexiconRepository {
    suspend fun getExistingWords(words: List<String>): List<String>
    suspend fun getWordsFromWords(words: List<String>): List<LexiconDto>
}
