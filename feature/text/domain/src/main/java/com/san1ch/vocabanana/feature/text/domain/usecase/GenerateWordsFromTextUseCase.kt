package com.san1ch.vocabanana.feature.text.domain.usecase

import com.san1ch.vocabanana.core.essentials.model.LexiconDto
import com.san1ch.vocabanana.core.essentials.model.fold
import com.san1ch.vocabanana.core.essentials.model.text.TextDomain
import com.san1ch.vocabanana.core.essentials.model.text.TextWordCount
import com.san1ch.vocabanana.core.essentials.model.word.PartOfSpeech
import com.san1ch.vocabanana.core.essentials.model.word.WordDomain
import com.san1ch.vocabanana.core.essentials.model.word.toPartOfSpeech
import com.san1ch.vocabanana.core.essentials.repositories.LemmatizationRepository
import com.san1ch.vocabanana.core.essentials.repositories.LexiconRepository
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GenerateWordsFromTextUseCase @Inject constructor(
    private val textRepository: TextRepository,
    private val tps: TextProcessingService,
    private val wordRepository: WordRepository,
    private val generateService: GenerateWordsFromTextService,
) {
    operator fun invoke(textId: Int): Flow<GenerateWordsFromTextState> = flow {
        // Step 1: Resource Gathering
        emit(GenerateWordsFromTextState.Loading.PreparingText)
        val text: TextDomain = textRepository.getTextsByIds(listOf(textId)).firstOrNull() ?: run {
            emit(GenerateWordsFromTextState.Error(GenerateWordsFromTextResult.Error.TextNotFound))
            return@flow
        }

        val wordFrequencies = tps.prepareText(text.content)
        val uniqueWords = wordFrequencies.keys.toList()

        // Step 2: Sorter (Filter out words already known to the user for dictionary adding)
        emit(GenerateWordsFromTextState.Loading.AnalyzingLexicon)
        val wordsToProcess = generateService.filterByUserVocab(uniqueWords)

        // Step 3: Builder (Generate dictionary domains for NEW words only)
        val domainsToAdd = if (wordsToProcess.isNotEmpty()) {
            generateService.generateDomains(wordsToProcess)
        } else {
            emptyList()
        }

        // Step 4: Storage & Complete Stats
        emit(GenerateWordsFromTextState.Loading.SavingWords)

        // 4a. Save new words to the dictionary if there are any
        if (domainsToAdd.isNotEmpty()) {
            wordRepository.addWords(domainsToAdd)
        }

        // 4b. Handle full text statistics (The "TextWordCount" entry)
        // We pass ALL unique words from the text, ensuring even known words get counted!
        if (uniqueWords.isNotEmpty()) {
            saveWordStats(textId, uniqueWords, wordFrequencies)
        }

        // Step 5: Finalize Emit
        if (domainsToAdd.isNotEmpty()) {
            emit(GenerateWordsFromTextState.Success(GenerateWordsFromTextResult.Success.Words(domainsToAdd)))
        } else {
            emit(GenerateWordsFromTextState.Success(GenerateWordsFromTextResult.Success.AllWordsAlreadyExists))
        }
    }

    private suspend fun saveWordStats(
        textId: Int,
        uniqueWords: List<String>,
        frequencies: Map<String, Int>,
    ) {
        val wordToDomainMap: Map<String, WordDomain> = wordRepository.getWordDomainsForWords(uniqueWords)

        val lemmaIdFrequencies = mutableMapOf<Int, Int>()

        frequencies.forEach { (word, count) ->
            val wordDomain = wordToDomainMap[word]

            if (wordDomain != null) {
                val lemmaId = wordDomain.id
                lemmaIdFrequencies[lemmaId] = (lemmaIdFrequencies[lemmaId] ?: 0) + count
            }
        }

        val statsToSave = lemmaIdFrequencies.map { (lemmaId, totalCount) ->
            TextWordCount(
                textId = textId,
                wordId = lemmaId,
                count = totalCount,
            )
        }

        textRepository.saveTextWordCounts(statsToSave)
    }
}

class GenerateWordsFromTextService @Inject constructor(
    private val wordRepository: WordRepository,
    private val lemmatizationRepository: LemmatizationRepository,
    private val lexiconRepository: LexiconRepository,
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
        output: MutableList<WordDomain>,
    ): List<String> {
        val pairs = lemmatizationRepository.getWordLemmaPairs(words)
        pairs.forEach { addValidated(it.lemma, listOf(it.word), posMap, output) }
        val processed = pairs.map { it.word }.toSet()
        return words.filter { it !in processed }
    }

    private suspend fun processExistingLemmas(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>,
    ): List<String> {
        val lemmas = lemmatizationRepository.findExistingLemmas(words)
        lemmas.forEach { addValidated(it, emptyList(), posMap, output) }
        return words.filter { it !in lemmas.toSet() }
    }

    private suspend fun processLexiconWords(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>,
    ): List<String> {
        val existing = lexiconRepository.getExistingWords(words)
        existing.forEach { addValidated(it, emptyList(), posMap, output) }
        return words.filter { it !in existing.toSet() }
    }

    private fun processRemainingUnknowns(
        words: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>,
    ) {
        words.forEach { addValidated(it, emptyList(), posMap, output) }
    }

    private fun addValidated(
        lemma: String,
        forms: List<String>,
        posMap: Map<String, LexiconDto>,
        output: MutableList<WordDomain>,
    ) {
        val dto = posMap[lemma]
        WordDomain.create(
            lemma = lemma,
            forms = forms,
            partOfSpeech = dto?.type?.toPartOfSpeech() ?: PartOfSpeech.UNKNOWN,
            definition = dto?.definition ?: "",
        ).fold(onSuccess = { output.add(it) }, onError = { })
    }
}

data class GenerateWordsFromTextData(
    val remainedWords: List<String>,
)

data class FixedData(
    val uniqueWords: Map<String, Int>,
    val userVocab: Set<String>,
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
    sealed class Success : GenerateWordsFromTextResult() {
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
