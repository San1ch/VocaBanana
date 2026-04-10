package com.example.vocabanana.feature.database.language.lemmatiazation

import com.example.vocabanana.core.database.LemmatizationRepository
import javax.inject.Inject

class LemmatizationRepositoryImpl @Inject constructor(
    private val lemmaDao: LemmaDao
) : LemmatizationRepository {
    override suspend fun getLemmasForWords(words: List<String>) =
        lemmaDao.getLemmasForWords(words)

    override suspend fun getLemmaForWord(word: String): String? =
        lemmaDao.getLemmaForWord(word)

    override suspend fun getWordLemmaPairs(words: List<String>) =
        lemmaDao.getWordLemmaPairs(words)

    override suspend fun findExistingLemmas(words: List<String>) =
        lemmaDao.findExistingLemmas(words)

    override suspend fun findExistingWords(words: List<String>): List<String> {
        return lemmaDao.findExistingWords(words)
    }

}