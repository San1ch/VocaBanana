package com.example.vocabanana.feature.text.domain.usecase

import com.example.vocabanana.core.language.TextProcessor
import com.example.vocabanana.core.domain.model.SentenceWithItsWords
import com.example.vocabanana.feature.database.word.repository.WordRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ParseTextToWordWithItsSentenceUseCase @Inject constructor(
    private val wordRepository: WordRepository,
    private val textProcessor: TextProcessor,
) {
    suspend operator fun invoke(text: String): List<SentenceWithItsWords> {

        // 1. Parse to sentences and tokenize
        val rawSentences = textProcessor.parseToSentences(text)
        val models = rawSentences.map{
            SentenceWithItsWords(
                sentence = it,
                words = textProcessor.tokenizeSentence(it)
            )
        }

        // 2. Get all words for future base checking
        val allRawWords = models.flatMap { it.words }.distinct()

        // 3. Pre-filtering of no normalized words by lemma or form from database

        val existedWordsInDataBase = wordRepository.getExistingWords(allRawWords).toSet()
        val wordsToNormalize = allRawWords.filter { it !in existedWordsInDataBase }

        // 4. Normalize words
        val normalizationMap = wordsToNormalize.associateWith { textProcessor.normalize(it) }

        // 5.Deduplicate after normalization and filter by lemma from database
        val candidateLemmas = normalizationMap.values.distinct()
        val existingLemmas = wordRepository.getExistingWords(candidateLemmas).toSet()

        // 6. Get known words
        val knownWords = (existedWordsInDataBase + existingLemmas).toMutableSet()

        // 7. Get unique new words in the model
        val seenInThisText = mutableSetOf<String>()
        return models.map { model ->
            val uniqueNewWords = model.words
                .map { raw -> normalizationMap[raw] ?: raw }
                .filter { it !in knownWords && it !in seenInThisText }
                .distinct()

            seenInThisText.addAll(uniqueNewWords)
            model.copy(words = uniqueNewWords)
        }.filter { it.words.isNotEmpty() }
    }
}

