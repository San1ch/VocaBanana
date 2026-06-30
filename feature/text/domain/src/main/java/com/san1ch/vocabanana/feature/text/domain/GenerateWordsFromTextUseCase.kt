package com.san1ch.vocabanana.feature.text.domain

import com.san1ch.vocabanana.core.essentials.database.model.LexiconDto
import com.san1ch.vocabanana.core.essentials.database.model.fold
import com.san1ch.vocabanana.core.essentials.database.model.text.TextDomain
import com.san1ch.vocabanana.core.essentials.database.model.word.PartOfSpeech
import com.san1ch.vocabanana.core.essentials.database.model.word.WordDomain
import com.san1ch.vocabanana.core.essentials.database.model.word.toPartOfSpeech
import com.san1ch.vocabanana.core.essentials.database.repositories.LemmatizationRepository
import com.san1ch.vocabanana.core.essentials.database.repositories.LexiconRepository
import com.san1ch.vocabanana.core.essentials.database.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.database.repositories.WordRepository
import com.san1ch.vocabanana.feature.text.domain.usecase.TextProcessingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty

class GenerateWordsFromTextUseCase @Inject constructor(
    private val textRepository: TextRepository,
    private val tps: TextProcessingService,
    private val wordRepository: WordRepository,
    private val generateService: GenerateWordsFromTextService
) {
    operator fun invoke(textId: Int): Flow<GenerateWordsFromTextState> = flow {
        // Step 1: Resource Gathering
        emit(GenerateWordsFromTextState.Loading.PreparingText)
        val text: TextDomain = textRepository.getTextById(textId).fold(
            onSuccess = { it },
            onFailure = {
                emit(
                    GenerateWordsFromTextState.Error(GenerateWordsFromTextResult.Error.TextNotFound)
                )
                return@flow
            }
        )
        val wordFrequencies = tps.prepareText(text.content)
        val uniqueWords = wordFrequencies.keys.toList()

        // Step 2: Sorter (Filter known words)
        emit(GenerateWordsFromTextState.Loading.AnalyzingLexicon)
        val wordsToProcess = generateService.filterByUserVocab(uniqueWords)

        if (wordsToProcess.isEmpty()) {
            emit(GenerateWordsFromTextState.Success(GenerateWordsFromTextResult.Success.AllWordsAlreadyExists))
            return@flow
        }

        // Step 3: Builder (The Dictionary Discovery)
        val domainsToAdd = generateService.generateDomains(wordsToProcess)

        // Step 4: Storage & Stats
        emit(GenerateWordsFromTextState.Loading.SavingWords)

        if (domainsToAdd.isNotEmpty()) {
            // 4a. Save the Words themselves (The "Dictionary")
            wordRepository.addWords(domainsToAdd)

            // 4b. Handle the Counts (The "Statistics")
            // Here you can call a separate repository to save the occurrences
            // using the 'textId' and 'wordFrequencies'
            saveWordStats(textId, domainsToAdd, wordFrequencies)

            emit(
                GenerateWordsFromTextState.Success(
                    GenerateWordsFromTextResult.Success.Words(
                        domainsToAdd
                    )
                )
            )
        } else {
            emit(GenerateWordsFromTextState.Error(GenerateWordsFromTextResult.Error.Unknown("No words found")))
        }
    }

    private suspend fun saveWordStats(
        textId: Int,
        domains: List<WordDomain>,
        frequencies: Map<String, Int>
    ) {
        // This is where you connect the Word to the Text.
        // Sum of Lemma count + Form counts.
        domains.forEach { domain ->
            val totalCount = (frequencies[domain.lemma] ?: 0) +
                    domain.forms.sumOf { frequencies[it] ?: 0 }

            // Example: wordRepository.saveOccurrence(textId, domain.lemma, totalCount)
        }
    }
}

class GenerateWordsFromTextService @Inject constructor(
    private val wordRepository: WordRepository,
    private val lemmatizationRepository: LemmatizationRepository,
    private val lexiconRepository: LexiconRepository
) {
    suspend fun filterByUserVocab(words: List<String>): List<String> {
        val userVocab = wordRepository.getAllLemmasAndForms().toSet()
        return words.filter { it !in userVocab }
    }

    suspend fun generateDomains(words: List<String>): List<WordDomain> {
        val lexiconMap = lexiconRepository.getWordsFromWords(words).associateBy { it.word }
        val output = mutableListOf<WordDomain>()

        // The Conveyor Belt: Stages 1-4
        var remaining = processLemmatizedPairs(words, lexiconMap, output)
        remaining = processExistingLemmas(remaining, lexiconMap, output)
        remaining = processLexiconWords(remaining, lexiconMap, output)
        processRemainingUnknowns(remaining, lexiconMap, output)

        return output
    }

    private suspend fun processLemmatizedPairs(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>
    ): List<String> {
        val pairs = lemmatizationRepository.getWordLemmaPairs(words)
        pairs.forEach { addValidated(it.lemma, listOf(it.word), posMap, output) }
        val processed = pairs.map { it.word }.toSet()
        return words.filter { it !in processed }
    }

    private suspend fun processExistingLemmas(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>
    ): List<String> {
        val lemmas = lemmatizationRepository.findExistingLemmas(words)
        lemmas.forEach { addValidated(it, emptyList(), posMap, output) }
        return words.filter { it !in lemmas.toSet() }
    }

    private suspend fun processLexiconWords(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>
    ): List<String> {
        val existing = lexiconRepository.getExistingWords(words)
        existing.forEach { addValidated(it, emptyList(), posMap, output) }
        return words.filter { it !in existing.toSet() }
    }

    private fun processRemainingUnknowns(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>
    ) {
        words.forEach { addValidated(it, emptyList(), posMap, output) }
    }

    private fun addValidated(
        lemma: String,
        forms: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>
    ) {
        val dto = posMap[lemma]
        WordDomain.create(
            lemma = lemma,
            forms = forms,
            partOfSpeech = dto?.type?.toPartOfSpeech() ?: PartOfSpeech.UNKNOWN,
            definition = dto?.definition ?: ""
        ).fold(onSuccess = { output.add(it) }, onError = { })
    }
}

data class GenerateWordsFromTextData(
    val remainedWords: List<String>
)

data class FixedData(
    val uniqueWords: Map<String, Int>,
    val userVocab: Set<String>
)


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
        object TextNotFound : Error()
        data class Unknown(val message: String) : Error()
    }
}
