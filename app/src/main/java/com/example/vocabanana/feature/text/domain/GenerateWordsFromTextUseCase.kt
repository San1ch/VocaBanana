package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.domain.TextToTextAiHub
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.feature.ai.texttotext.groq.AiResult
import com.example.vocabanana.feature.database.text.repository.TextRepository
import com.example.vocabanana.feature.database.word.repository.WordRepository
import com.example.vocabanana.feature.text.domain.usecase.ParseTextToWordWithItsSentenceUseCase
import com.example.vocabanana.feature.text.presentation.AiDtoToWordsUseCase
import com.example.vocabanana.feature.text.presentation.data.toUiText
import com.example.vocabanana.feature.word.domain.model.WordDomain
import javax.inject.Inject

class GenerateWordsFromTextUseCase @Inject constructor(
    private val textRepository: TextRepository,
    private val parseTextToWordWithItsSentence: ParseTextToWordWithItsSentenceUseCase,
    private val buildPromptsFromSentenceBatchesUseCase: BuildPromptsFromSentenceBatchesUseCase,
    private val textToTextAiHub: TextToTextAiHub,
    private val jsonParser: JsonTextToWordsParser,
    private val aiDtoToWordsUseCase: AiDtoToWordsUseCase,
    private val wordRepository: WordRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(textId: Int): GenerateWordsFromTextResult {
        // 1. Get text by id
        val targetText = textRepository.getTextById(textId).content
        logger.d("Target text: $targetText", tag = "GenerateWordsFromTextUseCase")

        // 2. Parse and clean at objects
        val sentencesWithWords = parseTextToWordWithItsSentence(targetText)
        if (sentencesWithWords.isEmpty()) {
            return GenerateWordsFromTextResult.Success.AllWordsAlreadyExists
        }
        logger.d("Sentences with words: ${sentencesWithWords.size}", tag = "GenerateWordsFromTextUseCase")

        // 3. Build prompts from objects
        val prompts = buildPromptsFromSentenceBatchesUseCase(sentencesWithWords, 50)
        logger.d("Prompts: ${prompts.size}", tag = "GenerateWordsFromTextUseCase")

        val totalWordsToProcess = sentencesWithWords.sumOf { it.wordsCount() }
        logger.d("Total words to process: $totalWordsToProcess", tag = "GenerateWordsFromTextUseCase")
        // 4. Send requests to AI

        val finalWords = mutableListOf<WordDomain>()
        var pendingError: GenerateWordsFromTextResult.Error? = null
        //TODO fix problem with "be" forms and problem with buildE type words
        logger.d("Starting AI requests", tag = "GenerateWordsFromTextUseCase")
        for (prompt in prompts) {
            when (val aiResult = textToTextAiHub.sendRequest(prompt)) {
                is AiResult.Success -> {
                    logger.d("AI request successful", tag = "GenerateWordsFromTextUseCase")
                    val dtos = jsonParser.parse(aiResult.data)
                    // 6. Convert dtos to words
                    when (val wordResult = aiDtoToWordsUseCase(dtos)) {
                        is GenerateWordsFromTextResult.Success.Words -> {
                            finalWords.addAll(wordResult.words)
                            logger.d("Added ${wordResult.words.size} words", tag = "GenerateWordsFromTextUseCase")
                        }

                        is GenerateWordsFromTextResult.Error -> {
                            pendingError = wordResult
                            logger.d("Error: $wordResult", tag = "GenerateWordsFromTextUseCase")
                            break
                        }

                        else -> {}
                    }
                }

                is AiResult.Error -> {
                    pendingError = mapAiError(aiResult)
                    logger.d("AI request failed: $aiResult", tag = "GenerateWordsFromTextUseCase")
                    break
                }
            }
        }

        if (finalWords.isNotEmpty()) {
            wordRepository.addWords(finalWords)
            logger.d("Added ${finalWords.size} words", tag = "GenerateWordsFromTextUseCase")
        }

        logger.d("Sending result", tag = "GenerateWordsFromTextUseCase")
        return when {
            pendingError != null -> if (finalWords.isNotEmpty()) {
                logger.d("Not all new words added ${finalWords.size} of $totalWordsToProcess", tag = "GenerateWordsFromTextUseCase")
                GenerateWordsFromTextResult.Error.NotAllNewWordsAdded(
                    addedCount = finalWords.size,
                    totalCount = totalWordsToProcess
                )
            } else {
                logger.d("Error: $pendingError", tag = "GenerateWordsFromTextUseCase")
                pendingError
            }

            finalWords.isEmpty() -> GenerateWordsFromTextResult.Success.AllWordsAlreadyExists
            else -> GenerateWordsFromTextResult.Success.Words(finalWords)
        }

    }

    private fun mapAiError(result: AiResult.Error): GenerateWordsFromTextResult.Error {
        return when (result) {
            AiResult.Error.InvalidApiKey -> GenerateWordsFromTextResult.Error.InvalidApiKey
            AiResult.Error.NetworkError -> GenerateWordsFromTextResult.Error.NetworkError
            AiResult.Error.RateLimitExceeded -> GenerateWordsFromTextResult.Error.RateLimitExceeded
            AiResult.Error.ServerError -> GenerateWordsFromTextResult.Error.ServerError
            is AiResult.Error.Unknown -> GenerateWordsFromTextResult.Error.Unknown(result.message)
        }
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



