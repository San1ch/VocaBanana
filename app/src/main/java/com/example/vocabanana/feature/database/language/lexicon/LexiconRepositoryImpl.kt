package com.example.vocabanana.feature.database.language.lexicon

import javax.inject.Inject

class LexiconRepositoryImpl @Inject constructor(
    private val lexiconDao: LexiconDao
) : LexiconRepository {
    override suspend fun getExistingWords(words: List<String>): List<String> {
        return lexiconDao.getExistingWords(words)
    }
}