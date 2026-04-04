package com.example.vocabanana.feature.text.domain.usecase

import android.util.Log
import com.example.vocabanana.core.database.word.repository.WordRepository
import com.example.vocabanana.core.language.WordNormalizer
import javax.inject.Inject

class ParseTextToWordWithSentenceUseCase @Inject constructor(
    private val wordRepository: WordRepository,
    private val wordNormalizer: WordNormalizer
) {
    private val TAG = "VocaBananaParse"

    suspend operator fun invoke(text: String): List<SentenceWithWords> {
        val startTime = System.currentTimeMillis()

        val rawSentences = parseToSentences(text)
        logTime("Parse sentences", startTime)

        val sentenceModels = createSentenceModels(rawSentences)
        logTime("Extract words", startTime)

        val allRawWords = sentenceModels.flatMap { it.words }.distinct()

        val existingInDb = wordRepository.getExistingWords(allRawWords).toSet()
        logTime("DB Check", startTime)

        val wordsToNormalize = allRawWords.filter { it !in existingInDb }
        val normalizationMap = wordsToNormalize.associateWith { wordNormalizer.normalize(it) }

        val candidateLemmas = normalizationMap.values.distinct()
        val existingLemmas = wordRepository.getExistingWords(candidateLemmas).toSet()

        val knownWords = (existingInDb + existingLemmas).toMutableSet()

        val seenInThisText = mutableSetOf<String>()

        val finalResult = sentenceModels.map { model ->
            val uniqueNewWords = model.words
                .map { raw -> normalizationMap[raw] ?: raw }
                .filter { it !in knownWords && it !in seenInThisText }
                .distinct()

            seenInThisText.addAll(uniqueNewWords)

            model.copy(words = uniqueNewWords)
        }.filter { it.words.isNotEmpty() }

        logTime("Total Processing", startTime)
        return finalResult
    }

    private fun logTime(message: String, startTime: Long) {
        Log.d(TAG, "Step [$message] took: ${System.currentTimeMillis() - startTime}ms")
    }

    private fun parseToSentences(text: String): List<String> {
        val sentenceRegex = Regex("(?<=[.!?])\\s+(?=[\\p{Lu}\\p{L}])|\\n+")
        return text.split(sentenceRegex)
            .filter { it.isNotBlank() }
            .map { it.trim() }
    }

    private fun createSentenceModels(sentences: List<String>): List<SentenceWithWords> {
        return sentences.map { sentence ->
            SentenceWithWords(
                sentence = sentence,
                words = extractCleanWords(sentence)
            )
        }
    }

    private fun extractCleanWords(sentence: String): List<String> {
        // 1. Розбиваємо на токени
        return sentence.split(Regex("[^\\p{L}'-]+"))
            .filter { it.isNotBlank() }
            .map { it.lowercase() }
            .map { word ->
                // 2. Обробка специфічних закінчень (скорочень)
                when {
                    word.endsWith("'re") -> word.removeSuffix("'re") // they're -> they
                    word.endsWith("'ve") -> word.removeSuffix("'ve") // I've -> i
                    word.endsWith("'m") -> word.removeSuffix("'m")   // I'm -> i
                    word.endsWith("'s") -> word.removeSuffix("'s")   // Amane's / It's -> amane / it
                    word.endsWith("'d") -> word.removeSuffix("'d")   // He'd -> he
                    word.endsWith("'ll") -> word.removeSuffix("'ll") // He'll -> he
                    else -> word
                }
            }
            .filter { it.length > 1 } // Прибираємо "i", "a", якщо вони лишилися після очищення
            .distinct()
    }
}

data class SentenceWithWords(
    val sentence: String,
    val words: List<String>
)