package com.san1ch.vocabanana.core.android.database.language.lexicon

import com.san1ch.vocabanana.core.essentials.database.model.LexiconDto
import com.san1ch.vocabanana.core.essentials.database.model.word.PartOfSpeech
import com.san1ch.vocabanana.core.essentials.database.repositories.LexiconRepository
import com.san1ch.vocabanana.feature.database.language.lexicon.LexiconDao
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



val LexiconEntity.toDto: LexiconDto
    get() = LexiconDto(word, type, definition)