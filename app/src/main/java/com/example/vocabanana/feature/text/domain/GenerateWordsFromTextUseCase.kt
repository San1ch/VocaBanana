package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.domain.TextToTextAiHub
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.feature.ai.texttotext.groq.AiResult
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.feature.text.domain.usecase.TextProcessingService
import com.example.vocabanana.feature.text.presentation.AiDtoToWordsUseCase
import com.example.vocabanana.feature.word.domain.model.WordDomain
import javax.inject.Inject

class GenerateWordsFromTextUseCase @Inject constructor(
    private val textRepository: TextRepository,
    private val textProcessingService: TextProcessingService,
    private val logger: Logger
) {
    suspend operator fun invoke(textId: Int): GenerateWordsFromTextResult {
        // 1. Get text by id
        val targetText = textRepository.getTextById(textId).content
        logger.d("Target text: $targetText", tag = "GenerateWordsFromTextUseCase")

        // 2. Parse and clean at objects
        val sentencesWithWords = textProcessingService.parseTextToWordWithItsSentence(targetText)
        if (sentencesWithWords.isEmpty()) {
            return GenerateWordsFromTextResult.Success.AllWordsAlreadyExists
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



