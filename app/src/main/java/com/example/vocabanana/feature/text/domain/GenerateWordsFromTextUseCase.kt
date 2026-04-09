package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.database.LemmatizationRepository
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.feature.text.domain.usecase.TextProcessingService
import com.example.vocabanana.feature.word.domain.model.WordDomain
import javax.inject.Inject
import kotlin.math.log

class GenerateWordsFromTextUseCase @Inject constructor(
    private val textRepository: TextRepository,
    private val logger: Logger,
    private val lemmatizationRepository: LemmatizationRepository,
    private val tps: TextProcessingService
) {
    suspend operator fun invoke(textId: Int) {
        val targetText = textRepository.getTextById(textId).content

        var currentList = tps.parseText(tps.cleanText(targetText)).distinct()
        logger.d("Words count: ${currentList.size}", "GenerateWordsFromTextUseCase")
        logger.d("============================================================", "GenerateWordsFromTextUseCase")

        val pairs = lemmatizationRepository.getWordLemmaPairs(currentList)
        logger.d("Pairs count: ${pairs.size}", "GenerateWordsFromTextUseCase")
        logger.d("============================================================", "GenerateWordsFromTextUseCase")

        val wordsFoundInDatabase = pairs.map { it.word }.toSet()
        logger.d("Words found in database: ${wordsFoundInDatabase.size}", "GenerateWordsFromTextUseCase")
        logger.d("============================================================", "GenerateWordsFromTextUseCase")

        val extractedLemmas = pairs.map { it.lemma }.toSet()

        currentList = currentList.filter { it !in wordsFoundInDatabase }
        logger.d("Filtered words count: ${currentList.size}: $currentList", "GenerateWordsFromTextUseCase")
        logger.d("============================================================", "GenerateWordsFromTextUseCase")

        val listWithoutTabletLemmas = lemmatizationRepository.findExistingLemmas(currentList)
        logger.d("List without tablet lemmas count: ${listWithoutTabletLemmas.size}: $listWithoutTabletLemmas", "GenerateWordsFromTextUseCase")
        logger.d("============================================================", "GenerateWordsFromTextUseCase")

        val withoutLemmasAndWord = currentList.filter { it !in listWithoutTabletLemmas }
        logger.d("Without lemmas and word count: ${withoutLemmasAndWord.size}: $withoutLemmasAndWord", "GenerateWordsFromTextUseCase")
        logger.d("============================================================", "GenerateWordsFromTextUseCase")
    }
}

sealed class GenerateWordsFromTextResult {
    sealed class Success() : GenerateWordsFromTextResult() {
        data class Words(val words: List<WordDomain>) : Success()
        object AllWordsAlreadyExists : Success()
    }

    sealed class Error : GenerateWordsFromTextResult() {
        object NetworkError : Error()
        object InvalidApiKey : Error()
        object RateLimitExceeded : Error()
        object ServerError : Error()
        data class NotAllNewWordsAdded(val addedCount: Int, val totalCount: Int) : Error()
        data class Unknown(val message: String) : Error()
    }
}



