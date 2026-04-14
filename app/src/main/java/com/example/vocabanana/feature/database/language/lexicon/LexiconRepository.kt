package com.example.vocabanana.feature.database.language.lexicon

import com.example.vocabanana.feature.word.domain.model.PartOfSpeech

interface LexiconRepository {
    suspend fun getExistingWords(words: List<String>): List<String>
    suspend fun getPartOfSpeeches(pos: List<String>): Map<String, PartOfSpeech>
}
