package com.example.vocabanana.feature.database.language.lexicon

import com.example.vocabanana.feature.word.domain.model.PartOfSpeech
import com.example.vocabanana.feature.word.domain.model.WordDomain
import javax.inject.Inject

class LexiconRepositoryImpl @Inject constructor(
    private val lexiconDao: LexiconDao
) : LexiconRepository {
    override suspend fun getExistingWords(words: List<String>): List<String> {
        return lexiconDao.getExistingWords(words)
    }

    override suspend fun getWordsFromWords(words: List<String>): List<LexiconDto> {
        return lexiconDao.getWordsFromWords(words).map { it.toDto }
    }
}


val String.toPartOfSpeech: PartOfSpeech
    get() = when {
        this == "adjective" -> PartOfSpeech.ADJECTIVE
        this == "noun" -> PartOfSpeech.NOUN
        this == "verb" -> PartOfSpeech.VERB
        this == "adverb" -> PartOfSpeech.ADVERB
        else -> PartOfSpeech.UNKNOWN
    }

data class LexiconDto(
    val word: String,
    val type: String,
    val definition: String
)

val LexiconEntity.toDto: LexiconDto
    get() = LexiconDto(word, type, definition)