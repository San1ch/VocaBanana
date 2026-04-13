package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.database.LemmatizationRepository
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.domain.model.fold
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.feature.database.language.lexicon.LexiconRepository
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
    suspend operator fun invoke(textId: Int) {
        val startOperationTime = System.currentTimeMillis()
        val targetText = textRepository.getTextById(textId).content

        logger.d("Words count: ${targetText.split(Regex("\\s+")).size}")

        // 1. Initial cleaning and preparation
        var remainWords = tps.prepareText(targetText)
        logger.d("Unique words list size: ${remainWords.size}", tag = "GenerateWordsFromTextUseCase")

        // 2. Filter out words already in the user's database
        remainWords = filterExistedWords(remainWords, wordRepository.getAllLemmasAndForms())
        logger.d("Unique words list without user vocab: ${remainWords.size}", tag = "GenerateWordsFromTextUseCase")

        val wordDomains = mutableListOf<WordDomain>()

        // 3. Process Lemmatized Pairs (Word -> Lemma)
        val lemmatizedWords = lemmaRep.getWordLemmaPairs(remainWords).map { pair ->
            WordDomain.create(
                lemma = pair.lemma,
                forms = listOf(pair.word),
                partOfSpeech = PartOfSpeech.UNKNOWN
            ).fold(
                onSuccess = { wordDomains.add(it) },
                onError = { logger.d("Validation failed for lemma pair ${pair.lemma}: $it") }
            )
            pair.word
        }
        remainWords = remainWords.filter { it !in lemmatizedWords }

        // 4. Process Clear Lemmas (Base forms already in Lemma DB)
        val clearLemmas = lemmaRep.findExistingLemmas(remainWords).map { lemma ->
            WordDomain.create(
                lemma = lemma,
                partOfSpeech = PartOfSpeech.UNKNOWN
            ).fold(
                onSuccess = { wordDomains.add(it) },
                onError = { logger.d("Validation failed for clear lemma $lemma: $it") }
            )
            lemma
        }
        remainWords = remainWords.filter { it !in clearLemmas }

        // 5. Process Lexicon Words (Found in dictionary but not Lemma DB)
        val lexiconFilteredWords = lexiconRepository.getExistingWords(remainWords).map { word ->
            WordDomain.create(
                lemma = word,
                partOfSpeech = PartOfSpeech.UNKNOWN
            ).fold(
                onSuccess = { wordDomains.add(it) },
                onError = { logger.d("Validation failed for lexicon word $word: $it") }
            )
            word
        }
        remainWords = remainWords.filter { it !in lexiconFilteredWords }

        // 6. Process Absolute Remainders (Unknowns/Names/New words)
        // These are important because the user might want to add them manually
        remainWords.forEach { unknownWord ->
            WordDomain.create(lemma = unknownWord, partOfSpeech = PartOfSpeech.UNKNOWN).fold(
                onSuccess = { wordDomains.add(it) },
                onError = { logger.d("Validation failed for unknown word $unknownWord: $it") }
            )
        }

        // 7. Final Logging and Saving
        logger.d("Final Remain list (Names/Unknowns): ${remainWords.size}", tag = "GenerateWordsFromTextUseCase")
        logger.d("Total WordDomains created: ${wordDomains.size}", tag = "GenerateWordsFromTextUseCase")

        if (wordDomains.isNotEmpty()) {
            wordRepository.addWords(wordDomains)
        }

        val endOperationTime = System.currentTimeMillis()
        logger.d("Operation time is: ${endOperationTime - startOperationTime}ms", tag = "GenerateWordsFromTextUseCase")
    }

    private fun filterNotExistedWords(filteringList: List<String>, filter: List<String>): List<String>{
        return filteringList.filter { !filter.contains(it) }
    }
    private fun filterExistedWords(filteringList: List<String>, filter: List<String>): List<String>{
        return filteringList.filter { filter.contains(it) }
    }

    // TODO
    //  1. Need add partOfSpeech finding or unknown
    //  2. Fix creating: Created to words with different forms. Need make fish: fishing, fish: fished -> fish: fishing, fished
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



