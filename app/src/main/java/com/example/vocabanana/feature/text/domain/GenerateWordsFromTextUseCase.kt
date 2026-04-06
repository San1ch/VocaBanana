package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.domain.TextToTextAiHub
import com.example.vocabanana.feature.ai.texttotext.groq.AiPromptBuilder
import com.example.vocabanana.feature.ai.texttotext.groq.AiResult
import com.example.vocabanana.feature.database.text.repository.TextRepository
import com.example.vocabanana.feature.database.word.repository.WordRepository
import com.example.vocabanana.feature.text.domain.usecase.ParseTextToWordWithItsSentenceUseCase
import com.example.vocabanana.feature.text.presentation.AiDtoToWordsUseCase
import com.example.vocabanana.feature.word.domain.model.WordDomain
import javax.inject.Inject

class GenerateWordsFromTextUseCase @Inject constructor(
    private val textRepository: TextRepository,
    private val parseTextToWordWithItsSentence: ParseTextToWordWithItsSentenceUseCase,
    private val promptBuilder: AiPromptBuilder,
    private val textToTextAiHub: TextToTextAiHub,
    private val jsonParser: JsonTextToWordsParser,
    private val aiDtoToWordsUseCase: AiDtoToWordsUseCase,
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(textId: Int): GenerateWordsFromTextResult {
        // 1. Get text by id
        val targetText = textRepository.getTextById(textId).content

        // 2. Parse and clean at objects
        val sentencesWithWords = parseTextToWordWithItsSentence(targetText)
        if(sentencesWithWords.isEmpty()){
            return GenerateWordsFromTextResult.Success.AllWordsAlreadyExists
        }

        // 3. Build prompt from objects
        //TODO NEED PARSING BY 50 WORDS
        val prompt = promptBuilder.createSentencesToWordsPrompt(sentencesWithWords)

        // 4. Send prompt to AI and process it
        when (val result = textToTextAiHub.sendRequest(prompt)) {
            is AiResult.Success -> {

                // 5. Parse response from JSON to dtos
                val dtos = jsonParser.parse(result.data)

                // 6. Save words to database
                when(val result = aiDtoToWordsUseCase(dtos)) {
                    is GenerateWordsFromTextResult.Success.Words -> {
                        wordRepository.addWords(result.words)
                        return result
                    }
                    else -> {
                        return result
                    }
                }
            }
            is AiResult.Error -> {
                return when(result) {
                    AiResult.Error.InvalidApiKey ->  GenerateWordsFromTextResult.Error.InvalidApiKey
                    AiResult.Error.NetworkError -> GenerateWordsFromTextResult.Error.NetworkError
                    AiResult.Error.RateLimitExceeded -> GenerateWordsFromTextResult.Error.RateLimitExceeded
                    AiResult.Error.ServerError -> GenerateWordsFromTextResult.Error.ServerError
                    is AiResult.Error.Unknown -> GenerateWordsFromTextResult.Error.Unknown(result.message)
                }
            }
        }
    }
}

sealed class GenerateWordsFromTextResult {
    sealed class Success() : GenerateWordsFromTextResult(){
        data class Words(val words: List<WordDomain>) : Success()
        object AllWordsAlreadyExists : Success()
    }
    sealed class Error : GenerateWordsFromTextResult() {
        object NetworkError : Error()
        object InvalidApiKey : Error()
        object RateLimitExceeded : Error()
        object ServerError : Error()
        data class Unknown(val message: String) : Error()
    }
}