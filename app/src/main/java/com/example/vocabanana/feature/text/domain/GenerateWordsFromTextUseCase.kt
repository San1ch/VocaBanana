package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.database.LemmatizationRepository
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.domain.model.fold
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.feature.database.language.lexicon.LexiconDto
import com.example.vocabanana.feature.database.language.lexicon.LexiconRepository
import com.example.vocabanana.feature.database.language.lexicon.toPartOfSpeech
import com.example.vocabanana.feature.text.domain.usecase.TextProcessingService
import com.example.vocabanana.feature.word.domain.model.PartOfSpeech
import com.example.vocabanana.feature.word.domain.model.WordDomain
import javax.inject.Inject

class GenerateWordsFromTextUseCase @Inject constructor(
    private val textRepository: TextRepository,
    private val wordRepository: WordRepository,
    private val logger: Logger,
    private val lemmaRep: LemmatizationRepository,
    private val tps: TextProcessingService,
    private val lexiconRepository: LexiconRepository
) {
    suspend operator fun invoke(textId: Int): Boolean {
        val startTime = System.currentTimeMillis()
        val content = textRepository.getTextById(textId).content

        val userVocab = wordRepository.getAllLemmasAndForms().toSet()
        val allUniqueWords = tps.prepareText(content)
        var remainWords = allUniqueWords.filter { it !in userVocab }

        // Fetch the DTOs and create a Map for O(1) lookup
        val lexiconMap = lexiconRepository.getWordsFromWords(remainWords)
            .associateBy { it.word }

        val wordDomains = mutableListOf<WordDomain>()

        // Pass the lexiconMap instead of the old posMap
        remainWords = processLemmatizedPairs(remainWords, lexiconMap, wordDomains)
        remainWords = processExistingLemmas(remainWords, lexiconMap, wordDomains)
        remainWords = processLexiconWords(remainWords, lexiconMap, wordDomains)
        processRemainingUnknowns(remainWords, lexiconMap, wordDomains)

        if (wordDomains.isNotEmpty()) {
            wordRepository.addWords(wordDomains)
        }

        logger.d("Processed in ${System.currentTimeMillis() - startTime}ms. Created: ${wordDomains.size}")
        return true
    }

    private suspend fun processLemmatizedPairs(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>
    ): List<String> {
        val pairs = lemmaRep.getWordLemmaPairs(words)
        pairs.forEach { pair ->
            addValidatedDomain(pair.lemma, listOf(pair.word), posMap, output)
        }
        val processed = pairs.map { it.word }.toSet()
        return words.filter { it !in processed }
    }

    private suspend fun processExistingLemmas(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>
    ): List<String> {
        val lemmas = lemmaRep.findExistingLemmas(words)
        lemmas.forEach { lemma ->
            addValidatedDomain(lemma, emptyList(), posMap, output)
        }
        val processed = lemmas.toSet()
        return words.filter { it !in processed }
    }

    private suspend fun processLexiconWords(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>
    ): List<String> {
        val existing = lexiconRepository.getExistingWords(words)
        existing.forEach { word ->
            addValidatedDomain(word, emptyList(), posMap, output)
        }
        val processed = existing.toSet()
        return words.filter { it !in processed }
    }

    private fun processRemainingUnknowns(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>
    ) {
        words.forEach { addValidatedDomain(it, emptyList(), posMap, output) }
    }

    private fun addValidatedDomain(
        lemma: String,
        forms: List<String>,
        lexiconMap: Map<String, LexiconDto>, // Updated parameter
        output: MutableList<WordDomain>
    ) {
        val dto = lexiconMap[lemma]

        WordDomain.create(
            lemma = lemma,
            forms = forms,
            // Convert String from DTO to Domain Enum
            partOfSpeech = dto?.type?.toPartOfSpeech ?: PartOfSpeech.UNKNOWN,
            // Add definition from DTO, or empty if it doesn't exist
            definition = dto?.definition ?: ""
        ).fold(
            onSuccess = { output.add(it) },
            onError = { logger.d("Validation failed for $lemma: $it") }
        )
    }

    // TODO
    //  1. Move word domain logic to the [core]
    //
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



