package com.example.vocabanana.feature.database.language.lexicon

import com.example.vocabanana.feature.word.domain.model.PartOfSpeech
import javax.inject.Inject

class LexiconRepositoryImpl @Inject constructor(
    private val lexiconDao: LexiconDao
) : LexiconRepository {
    override suspend fun getExistingWords(words: List<String>): List<String> {
        return lexiconDao.getExistingWords(words)
    }
    override suspend fun getPartOfSpeeches(pos: List<String>): Map<String, PartOfSpeech> {
        val pairs = lexiconDao.getPartOfSpeeches(pos)
        return pairs.mapValues { (key, value) ->
            value.toPartOfSpeech
        }
    }
}

val String.toPartOfSpeech: PartOfSpeech
    get() = when{
        this == "adjective" -> PartOfSpeech.ADJECTIVE
        this == "noun" -> PartOfSpeech.NOUN
        this == "verb" -> PartOfSpeech.VERB
        this == "adverb" -> PartOfSpeech.ADVERB
        else -> PartOfSpeech.UNKNOWN
    }