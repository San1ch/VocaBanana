package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.database.LemmatizationRepository
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.domain.model.fold
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.core.word.domain.model.PartOfSpeech
import com.example.vocabanana.core.word.domain.model.WordDomain
import com.example.vocabanana.feature.database.language.lexicon.LexiconDto
import com.example.vocabanana.feature.database.language.lexicon.LexiconRepository
import com.example.vocabanana.feature.database.language.lexicon.toPartOfSpeech
import com.example.vocabanana.feature.text.domain.usecase.TextProcessingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GenerateWordsFromTextUseCase @Inject constructor(
    private val textRepository: TextRepository,
    private val wordRepository: WordRepository,
    private val logger: Logger,
    private val lemmaRep: LemmatizationRepository,
    private val tps: TextProcessingService,
    private val lexiconRepository: LexiconRepository
) {
    operator fun invoke(textId: Int): Flow<GenerateWordsFromTextState> = flow {
        emit(GenerateWordsFromTextState.Loading.PreparingText)
        val content = textRepository.getTextById(textId).content

        // 1. Get frequencies: Map<Word, Count>
        val wordFrequencies = tps.prepareText(content)
        val allUniqueWords = wordFrequencies.keys.toList()

        emit(GenerateWordsFromTextState.Loading.AnalyzingLexicon)
        val userVocab = wordRepository.getAllLemmasAndForms().toSet()

        // Filter words the user doesn't have yet
        var remainWords = allUniqueWords.filter { it !in userVocab }

        if (remainWords.isEmpty()) {
            emit(GenerateWordsFromTextState.Success(GenerateWordsFromTextResult.Success.AllWordsAlreadyExists))
            return@flow
        }

        val lexiconMap = lexiconRepository.getWordsFromWords(remainWords).associateBy { it.word }
        val wordDomains = mutableListOf<WordDomain>()

        // 2. RUN DISCOVERY: Pass frequencies to lookup counts during processing
        remainWords = processLemmatizedPairs(remainWords, lexiconMap, wordDomains, wordFrequencies)
        remainWords = processExistingLemmas(remainWords, lexiconMap, wordDomains, wordFrequencies)
        remainWords = processLexiconWords(remainWords, lexiconMap, wordDomains, wordFrequencies)
        processRemainingUnknowns(remainWords, lexiconMap, wordDomains, wordFrequencies)

        emit(GenerateWordsFromTextState.Loading.SavingWords)
        if (wordDomains.isNotEmpty()) {
            wordRepository.addWords(wordDomains)
            emit(GenerateWordsFromTextState.Success(GenerateWordsFromTextResult.Success.Words(wordDomains)))
        } else {
            emit(GenerateWordsFromTextState.Error(GenerateWordsFromTextResult.Error.Unknown("No valid words found")))
        }
    }

    // Stage 1: Lemmatization pairs (e.g., "running" -> "run")
    private suspend fun processLemmatizedPairs(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>,
        frequencies: Map<String, Int>
    ): List<String> {
        val pairs = lemmaRep.getWordLemmaPairs(words)
        pairs.forEach { pair ->
            val count = frequencies[pair.word] ?: 1
            addValidatedDomain(pair.lemma, listOf(pair.word), posMap, output, count)
        }
        val processed = pairs.map { it.word }.toSet()
        return words.filter { it !in processed }
    }

    // Stage 2: Words that are already lemmas
    private suspend fun processExistingLemmas(
        words: List<String>,
        lexiconMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>,
        frequencies: Map<String, Int>
    ): List<String> {
        val lemmas = lemmaRep.findExistingLemmas(words)
        lemmas.forEach { lemma ->
            val count = frequencies[lemma] ?: 1
            addValidatedDomain(lemma, emptyList(), lexiconMap, output, count)
        }
        val processed = lemmas.toSet()
        return words.filter { it !in processed }
    }

    // Stage 3: Direct Lexicon lookup
    private suspend fun processLexiconWords(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>,
        frequencies: Map<String, Int>
    ): List<String> {

        val existing = lexiconRepository.getExistingWords(words)
        existing.forEach { word ->
            val count = frequencies[word] ?: 1
            addValidatedDomain(word, emptyList(), posMap, output, count)
        }
        val processed = existing.toSet()
        return words.filter { it !in processed }
    }

    // Stage 4: Everything else (Unknowns)
    private fun processRemainingUnknowns(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>,
        frequencies: Map<String, Int>
    ) {
        words.forEach { word ->
            val count = frequencies[word] ?: 1
            addValidatedDomain(word, emptyList(), posMap, output, count)
        }
    }

    private fun addValidatedDomain(
        lemma: String,
        forms: List<String>,
        lexiconMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>,
        count: Int
    ) {

        val dto = lexiconMap[lemma]
        WordDomain.create(
            lemma = lemma,
            forms = forms,
            partOfSpeech = dto?.type?.toPartOfSpeech ?: PartOfSpeech.UNKNOWN,
            definition = dto?.definition ?: "",
            countInTheTexts = count
        ).fold(
            onSuccess = { output.add(it) },
            onError = { logger.d("Validation failed for $lemma: $it") }
        )
    }
}



sealed class GenerateWordsFromTextState {
    // We use data object for simple stages
    sealed class Loading : GenerateWordsFromTextState() {

        data object PreparingText : Loading()
        data object AnalyzingLexicon : Loading()
        data object SavingWords : Loading()
    }

    data class Success(val result: GenerateWordsFromTextResult.Success) : GenerateWordsFromTextState()
    data class Error(val error: GenerateWordsFromTextResult.Error) : GenerateWordsFromTextState()
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



