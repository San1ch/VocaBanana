package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.database.LemmatizationRepository
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.feature.database.language.lexicon.LexiconRepository
import com.example.vocabanana.feature.text.domain.usecase.TextProcessingService
import com.example.vocabanana.feature.word.domain.model.WordDomain
import javax.inject.Inject

class GenerateWordsFromTextUseCase @Inject constructor(
    private val textRepository: TextRepository,
    private val logger: Logger,
    private val lemmaRep: LemmatizationRepository,
    private val tps: TextProcessingService,
    private val lexiconRepository: LexiconRepository
) {
    suspend operator fun invoke(textId: Int) {
        val startOperationTime = System.currentTimeMillis()
        val targetText = textRepository.getTextById(textId).content
        val currentList = prepareWords(targetText)
        logger.d("Current list size: ${currentList.size}", tag = "GenerateWordsFromTextUseCase")

        val withoutExistingWords = filterNotExistedWords(currentList, lemmaRep.findExistingWords(currentList))
        logger.d("Without existing words: ${withoutExistingWords.size}:", tag = "GenerateWordsFromTextUseCase")
        //logger.d("Words: ${withoutExistingWords.joinToString(", ")}", tag = "GenerateWordsFromTextUseCase")

        val withoutExistingLemmas = filterNotExistedWords(withoutExistingWords, lemmaRep.findExistingLemmas(withoutExistingWords))
        logger.d("Without existing lemmas: ${withoutExistingLemmas.size}:", tag = "GenerateWordsFromTextUseCase")
        //logger.d("Words: ${withoutExistingLemmas.joinToString(", ")}", tag = "GenerateWordsFromTextUseCase")

        val withoutExistingLexicons = filterNotExistedWords(withoutExistingLemmas, lexiconRepository.getExistingWords(withoutExistingLemmas))
        logger.d("Without existing lexicons: ${withoutExistingLexicons.size}:", tag = "GenerateWordsFromTextUseCase")
        logger.d("Words: ${withoutExistingLexicons.joinToString(", ")}", tag = "GenerateWordsFromTextUseCase")

        val endOperationTime = System.currentTimeMillis()

        logger.d("Operation time is: ${endOperationTime - startOperationTime}ms", tag = "GenerateWordsFromTextUseCase")


        // 1. I need filter number
        // 2. I need make another table for names, places and other things with name
        // 3.
    }

    private fun filterNotExistedWords(previous: List<String>, existed: List<String>): List<String>{
        return previous.filter { !existed.contains(it) }
    }

    private fun prepareWords(text: String): List<String>{
        var text = tps.normalizeGrammar(text)
        text = tps.cleanText(text)
        text = tps.hyphenWordsNormalization(text)
        val words = tps.parseText(text)
        return words.distinct()
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



