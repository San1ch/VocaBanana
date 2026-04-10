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
        val targetText = textRepository.getTextById(textId).content
        val currentList = prepareWords(targetText)
        logger.d("Current list size: ${currentList.size}", tag = "GenerateWordsFromTextUseCase")

        val notExistingWords = filterNotExistedWords(currentList, lemmaRep.findExistingWords(currentList))
        logger.d("Not existing words: ${notExistingWords.size}:", tag = "GenerateWordsFromTextUseCase")
        logger.d("Words: ${notExistingWords.joinToString(", ")}", tag = "GenerateWordsFromTextUseCase")

        val notExistingLemmas = filterNotExistedWords(notExistingWords, lemmaRep.findExistingLemmas(notExistingWords))
        logger.d("Not existing lemmas: ${notExistingLemmas.size}:", tag = "GenerateWordsFromTextUseCase")
        logger.d("Words: ${notExistingLemmas.joinToString(", ")}", tag = "GenerateWordsFromTextUseCase")

        val notExistingLexicons = filterNotExistedWords(notExistingLemmas, lexiconRepository.getExistingWords(notExistingLemmas))
        logger.d("Not existing lexicons: ${notExistingLexicons.size}:", tag = "GenerateWordsFromTextUseCase")
        logger.d("Words: ${notExistingLexicons.joinToString(", ")}", tag = "GenerateWordsFromTextUseCase")
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



