package com.san1ch.vocabanana.core.android.database.language.lemmatiazation

import com.san1ch.vocabanana.core.essentials.database.repositories.LemmatizationRepository
import javax.inject.Inject

class LemmatizationRepositoryImpl @Inject constructor(
    private val lemmaDao: LemmaDao
) : LemmatizationRepository {
    override suspend fun getLemmasForWords(words: List<String>) =
        lemmaDao.getLemmasForWords(words)

    override suspend fun getLemmaForWord(word: String): Result<String> {
        if (word.isEmpty()) return Result.failure(Exception("Word is empty"))
        return when(val word = lemmaDao.getLemmaForWord(word)){
            null -> Result.failure(Exception("Word not found"))
            else -> Result.success(word)
        }
    }

    override suspend fun getWordLemmaPairs(words: List<String>) =
        lemmaDao.getWordLemmaPairs(words)

    override suspend fun findExistingLemmas(words: List<String>) =
        lemmaDao.findExistingLemmas(words)

    override suspend fun findExistingWords(words: List<String>): List<String> {
        return lemmaDao.findExistingWords(words)
    }

}