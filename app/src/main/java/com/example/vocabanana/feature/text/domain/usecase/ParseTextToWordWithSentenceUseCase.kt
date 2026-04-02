package com.example.vocabanana.feature.text.domain.usecase

import com.example.vocabanana.core.database.word.repository.WordRepository
import com.example.vocabanana.core.language.WordNormalizer
import javax.inject.Inject

class ParseTextToWordWithSentenceUseCase @Inject constructor(
    private val wordRepository: WordRepository,
    private val wordNormalizer: WordNormalizer
) {
    suspend operator fun invoke(text: String): List<SentenceWithWords> {
        val rawSentences = parseToSentences(text)
        val sentenceModels = createSentenceModels(rawSentences)

        val normalizedModels = sentenceModels.map { model ->
            model.copy(
                words = model.words.map { wordNormalizer.normalize(it) }
            )
        }

        val uniqueInText = filterUniqueWordsInText(normalizedModels)

        return cleanExistingWords(uniqueInText)
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

    private fun filterUniqueWordsInText(models: List<SentenceWithWords>): List<SentenceWithWords> {
        val seenWords = mutableSetOf<String>()

        return models.map { model ->
            val uniqueWords = model.words.filter { word ->
                if (word !in seenWords) {
                    seenWords.add(word)
                    true
                } else false
            }
            model.copy(words = uniqueWords)
        }.filter { it.words.isNotEmpty() }
    }

    private suspend fun cleanExistingWords(models: List<SentenceWithWords>): List<SentenceWithWords> {
        val allWordsInText = models.flatMap { it.words }.distinct()

        if (allWordsInText.isEmpty()) return emptyList()

        val existingWords = wordRepository.getExistingWords(allWordsInText)

        return models.map { model ->
            model.copy(
                words = model.words.filter { it !in existingWords }
            )
        }.filter { it.words.isNotEmpty() }
    }

    private fun extractCleanWords(sentence: String): List<String> {
        return sentence.split(Regex("[^\\p{L}-]+"))
            .filter { it.isNotBlank() && it.length > 1 }
            .map { it.lowercase() }
    }
}

data class SentenceWithWords(
    val sentence: String,
    val words: List<String>
)