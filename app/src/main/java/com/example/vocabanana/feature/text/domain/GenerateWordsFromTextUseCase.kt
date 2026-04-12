package com.example.vocabanana.feature.text.domain

import com.example.vocabanana.core.database.LemmatizationRepository
import com.example.vocabanana.core.database.TextRepository
import com.example.vocabanana.core.database.WordRepository
import com.example.vocabanana.core.utilities.logs.Logger
import com.example.vocabanana.feature.database.language.lexicon.LexiconRepository
import com.example.vocabanana.feature.text.domain.usecase.TextProcessingService
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

        //TODO  1. need filter for already existed words
        //      2. need add word saving
        //      3. perhaps need add words counting (how many words were in the texts)

        var allWords = prepareWords(targetText)
        logger.d("Unique words list size: ${allWords.size}", tag = "GenerateWordsFromTextUseCase")

        val lemmas: MutableList<String> = emptyList<String>().toMutableList()

        val lemmatizedWords = lemmaRep.getWordLemmaPairs(allWords).map{
            lemmas.add(it.lemma)
            it.word
        }
        // I need to remove lemmatizedWords from all words
        allWords = allWords.filter { !lemmatizedWords.contains(it) }
        logger.d("Lemmatized words list size: ${lemmatizedWords.size}", tag = "GenerateWordsFromTextUseCase")
        logger.d("All words list size: ${allWords.size}", tag = "GenerateWordsFromTextUseCase")

        val clearLemmas = lemmaRep.findExistingLemmas(allWords).map{
            lemmas.add(it)
            it
        }
        allWords = allWords.filter { !clearLemmas.contains(it) }
        logger.d("Clear lemmas list size: ${clearLemmas.size}", tag = "GenerateWordsFromTextUseCase")
        logger.d("All words list size: ${allWords.size}", tag = "GenerateWordsFromTextUseCase")

        val lexiconFilteredWords = lexiconRepository.getExistingWords(allWords).map{
            lemmas.add(it)
            it
        }

        allWords = allWords.filter { !lexiconFilteredWords.contains(it) }
        logger.d("Lexicon filtered words list size: ${lexiconFilteredWords.size}", tag = "GenerateWordsFromTextUseCase")
        logger.d("All words list size: ${allWords.size}", tag = "GenerateWordsFromTextUseCase")

        val lexiconWordCount = lexiconRepository.getExistingWords(lemmas).size
        logger.d("Lexicon word count: $lexiconWordCount", tag = "GenerateWordsFromTextUseCase")









        // for tests

        //val withoutExistingWords = filterNotExistedWords(uniqueWords, lemmaRep.findExistingWords(uniqueWords))
        //logger.d("Without existing words: ${withoutExistingWords.size}:", tag = "GenerateWordsFromTextUseCase")
        //logger.d("Words: ${withoutExistingWords.joinToString(", ")}", tag = "GenerateWordsFromTextUseCase")

        //val withoutExistingLemmas = filterNotExistedWords(withoutExistingWords, lemmaRep.findExistingLemmas(withoutExistingWords))
        //logger.d("Without existing lemmas: ${withoutExistingLemmas.size}:", tag = "GenerateWordsFromTextUseCase")
        //logger.d("Words: ${withoutExistingLemmas.joinToString(", ")}", tag = "GenerateWordsFromTextUseCase")

        //val withoutExistingLexicons = filterNotExistedWords(withoutExistingLemmas, lexiconRepository.getExistingWords(withoutExistingLemmas))
        //logger.d("Without existing lexicons: ${withoutExistingLexicons.size}:", tag = "GenerateWordsFromTextUseCase")
        //logger.d("Words: ${withoutExistingLexicons.joinToString(", ")}", tag = "GenerateWordsFromTextUseCase")

        val endOperationTime = System.currentTimeMillis()
        logger.d("Operation time is: ${endOperationTime - startOperationTime}ms", tag = "GenerateWordsFromTextUseCase")


    }

    private fun filterNotExistedWords(previous: List<String>, existed: List<String>): List<String>{
        return previous.filter { !existed.contains(it) }
    }

    private fun prepareWords(text: String): List<String>{
        var text = tps.normalizeGrammar(text)
        text = tps.cleanText(text)
        text = tps.hyphenWordsNormalization(text)
        val words = tps.parseText(text)
        return words.distinct()
    }
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



