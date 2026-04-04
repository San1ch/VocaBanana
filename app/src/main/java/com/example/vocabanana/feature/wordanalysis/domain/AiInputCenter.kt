package com.example.vocabanana.feature.wordanalysis.domain

import android.util.Log
import com.example.vocabanana.core.ai.AiCenter
import com.example.vocabanana.core.data.ValidateResult
import com.example.vocabanana.core.database.word.repository.WordRepository
import com.example.vocabanana.feature.text.domain.usecase.SentenceWithWords
import com.example.vocabanana.feature.word.domain.PartOfSpeech
import com.example.vocabanana.feature.word.domain.WordDomain
import com.example.vocabanana.feature.word.domain.WordFormDomain
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton


data class PromptBatch(
    val promptText: String,
    val inputWords: List<String>
)

@Singleton
class AiInputCenter @Inject constructor(
    private val aiPromptBuilder: AiPromptBuilder,
    private val aiCenter: AiCenter,
    private val aiResponseParser: AiResponseParser,
    private val wordRepository: WordRepository,
    private val wordTestCorrectionCenter: WordTestCorrectionCenter
) {

    private var pendingSentences = mutableListOf<SentenceWithWords>()

    private var currentLimits = AiInputCenterDataLength(
        charsLimit = 1200,
        sentencesLimit = 15
    )

    private var lastBatch = emptyList<SentenceWithWords>()

    // 1. Виносимо базову затримку як змінну класу (або передаємо між функціями),
// щоб вона "запам'ятовувала" навантаження.
    private var globalDelay = 3000L

    suspend fun startTests(
        list: List<SentenceWithWords>,
        firstWordCount: Int,
        secondWordCount: Int,
        thirdWordCount: Int,
    ): List<WordInfoValidationLog> {
        // Скидаємо перед новим великим тестом, якщо хочеш, або залишаємо як є
        globalDelay = 3000L

        val firstBatches = getPromptsByParameters(list, firstWordCount)
        val secondBatches = getPromptsByParameters(list, secondWordCount)
        val thirdBatches = getPromptsByParameters(list, thirdWordCount)

        // Тепер currentDelay буде "рости" від тесту до тесту
        val firstLogs = sendAndGetData(firstBatches, "Batch-$firstWordCount")
        val secondLogs = sendAndGetData(secondBatches, "Batch-$secondWordCount")
        val thirdLogs = sendAndGetData(thirdBatches, "Batch-$thirdWordCount")

        return firstLogs + secondLogs + thirdLogs
    }

    suspend fun sendAndGetData(
        batches: List<PromptBatch>,
        validationName: String
    ): List<WordInfoValidationLog> {
        val systemPrompt = aiPromptBuilder.buildSystemPrompt()
        val logs = mutableListOf<WordInfoValidationLog>()

        batches.forEachIndexed { index, batch ->
            wordTestCorrectionCenter.changeWorkingStage(
                currentBatchStage = index + 1,
                maxBatchStage = batches.size
            )

            var isSuccess = false
            var rawResponse = ""

            while (!isSuccess) {
                val result = aiCenter.sendRequest(systemPrompt, batch.promptText)

                if (result.contains("429") || result.contains("Too Many Requests", ignoreCase = true)) {
                    // План "Назавжди": збільшуємо базову затримку на +1 чи +2 сек
                    globalDelay += 1000L
                    Log.w("AI_TEST", "⚠️ 429! Збільшуємо постійну затримку до: ${globalDelay}ms")

                    // Чекаємо актуальний delay прямо зараз
                    delay(globalDelay)
                }
                else if (result.startsWith("Error") || result.isEmpty()) {
                    Log.e("AI_TEST", "❌ Помилка: $result. Спробуємо через 5 сек.")
                    delay(5000L)
                }
                else {
                    rawResponse = result
                    isSuccess = true
                    Log.i("AI_TEST", "✅ Отримано: Batch ${index + 1}. Поточна пауза: ${globalDelay}ms")
                }
            }

            val outputWords = aiResponseParser.parse(rawResponse).map { it.word }

            logs.add(
                WordInfoValidationLog(
                    validationName = validationName,
                    inputWords = batch.inputWords,
                    outputWords = outputWords,
                    prompt = batch.promptText,
                    response = rawResponse,
                    timestamp = System.currentTimeMillis(),
                )
            )
            delay(globalDelay)
        }
        return logs
    }

    fun getPromptsByParameters(list: List<SentenceWithWords>, wordCount: Int): List<PromptBatch> {
        val batches = mutableListOf<PromptBatch>()
        val currentBatchSentences = mutableListOf<SentenceWithWords>()
        var currentSize = 0

        list.forEach { sentence ->
            if (currentSize + sentence.words.size > wordCount && currentBatchSentences.isNotEmpty()) {
                batches.add(
                    PromptBatch(
                        promptText = aiPromptBuilder.buildData(currentBatchSentences),
                        inputWords = currentBatchSentences.flatMap { it.words }
                    )
                )
                currentBatchSentences.clear()
                currentSize = 0
            }
            currentBatchSentences.add(sentence)
            currentSize += sentence.words.size
        }

        if (currentBatchSentences.isNotEmpty()) {
            batches.add(
                PromptBatch(
                    promptText = aiPromptBuilder.buildData(currentBatchSentences),
                    inputWords = currentBatchSentences.flatMap { it.words }
                )
            )
        }
        return batches
    }

    suspend fun prepareAndStart(list: List<SentenceWithWords>): AiMetaDataResult {
        Log.d("AiInputCenter", "Prepared sentences: ${list.size}")
        return processNextBatch()
    }

    suspend fun processNextBatch(): AiMetaDataResult {
        if (pendingSentences.isEmpty()) {
            return AiMetaDataResult(
                resultText = "Parsing is completed.",
                fullPrompt = "",
                lastWordCount = 0,
                lastSentenceCount = 0,
                lastCharsCount = 0,
                currentCharsLimit = 0,
                promptLengthChars = 0,
                responseLengthChars = 0,
                jsonWordsCount = 0
            )
        }

        val batch = mutableListOf<SentenceWithWords>()
        var totalCharsInBatch = 0
        var totalWordsInBatch = 0

        val iterator = pendingSentences.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            val sentenceLen = item.sentence.length

            val canAddMoreSentences = batch.size < currentLimits.sentencesLimit
            val canAddMoreChars = (totalCharsInBatch + sentenceLen) <= currentLimits.charsLimit

            if (canAddMoreSentences && canAddMoreChars) {
                batch.add(item)
                totalCharsInBatch += sentenceLen
                totalWordsInBatch += item.words.size
            } else if (batch.isEmpty()) {

                batch.add(item)
                totalCharsInBatch += sentenceLen
                totalWordsInBatch += item.words.size
                break
            } else {
                break
            }
        }

        lastBatch = batch

        val systemPrompt = aiPromptBuilder.buildSystemPrompt()
        val userData = aiPromptBuilder.buildData(batch)
        val fullPrompt = "$systemPrompt\n\n--- DATA ---\n\n$userData"

        return try {
            val aiResponse = aiCenter.sendRequest(systemPrompt, userData)


            val jsonCount = saveProcessedBatch(aiResponse)

            AiMetaDataResult(
                resultText = aiResponse,
                fullPrompt = fullPrompt,
                lastWordCount = totalWordsInBatch,
                lastSentenceCount = batch.size,
                lastCharsCount = totalCharsInBatch,
                currentCharsLimit = currentLimits.charsLimit,
                promptLengthChars = fullPrompt.length,
                responseLengthChars = aiResponse.length,
                jsonWordsCount = jsonCount
            )
        } catch (e: Exception) {
            Log.e("AiInputCenter", "Error in processNextBatch: ${e.message}")
            AiMetaDataResult(
                resultText = "Error: ${e.message}",
                fullPrompt = fullPrompt,
                lastWordCount = totalWordsInBatch,
                lastSentenceCount = batch.size,
                lastCharsCount = totalCharsInBatch,
                currentCharsLimit = currentLimits.charsLimit,
                promptLengthChars = fullPrompt.length,
                responseLengthChars = 0,
                jsonWordsCount = 0
            )
        }
    }


    suspend fun saveProcessedBatch(rawJson: String): Int {
        val dtos = aiResponseParser.parse(rawJson)
        if (dtos.isEmpty()) return 0

        val wordsToSave = dtos.mapNotNull { dto ->
            val cleanLemma = dto.lemma.lowercase().trim()
            val cleanWord = dto.word.lowercase().trim()

            val rawForms = if (cleanWord != cleanLemma) {
                listOf(
                    WordFormDomain.create(
                        id = 0, wordId = 0, form = cleanWord,
                        partOfSpeech = PartOfSpeech.fromShortName(dto.wordPos)
                    )
                )
            } else emptyList()

            val validatedForms = rawForms.mapNotNull { result ->
                if (result is ValidateResult.Success) result.value else null
            }

            val wordResult = WordDomain.create(
                lemma = cleanLemma,
                partOfSpeech = PartOfSpeech.fromShortName(dto.lemmaPos),
                forms = validatedForms
            )

            if (wordResult is ValidateResult.Success) wordResult.value else null
        }.distinctBy { it.lemma }

        if (wordsToSave.isNotEmpty()) {
            wordRepository.addWords(wordsToSave)
        }


        handleFeedback(wasIncorrect = false)

        return dtos.size
    }

    fun handleFeedback(wasIncorrect: Boolean) {
        if (wasIncorrect) {

            currentLimits = currentLimits.copy(
                charsLimit = (currentLimits.charsLimit * 0.8).toInt().coerceAtLeast(300),
                sentencesLimit = (currentLimits.sentencesLimit - 2).coerceAtLeast(1)
            )
        } else {

            pendingSentences.removeAll { pending ->
                lastBatch.any { it.sentence == pending.sentence }
            }
            lastBatch = emptyList()


            currentLimits = currentLimits.copy(
                charsLimit = (currentLimits.charsLimit + 100).coerceAtMost(2500),
                sentencesLimit = (currentLimits.sentencesLimit + 1).coerceAtMost(30)
            )
        }
    }
}

data class AiInputCenterDataLength(
    val charsLimit: Int,
    val sentencesLimit: Int
)

data class AiMetaDataResult(
    val resultText: String,
    val fullPrompt: String,
    val lastWordCount: Int,
    val lastSentenceCount: Int,
    val lastCharsCount: Int,
    val currentCharsLimit: Int,
    val promptLengthChars: Int,
    val responseLengthChars: Int,
    val jsonWordsCount: Int
)