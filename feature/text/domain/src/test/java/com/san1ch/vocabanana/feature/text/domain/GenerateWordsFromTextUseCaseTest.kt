package com.san1ch.vocabanana.feature.text.domain

import com.san1ch.vocabanana.core.essentials.model.text.TextDomain
import com.san1ch.vocabanana.core.essentials.model.word.PartOfSpeech
import com.san1ch.vocabanana.core.essentials.model.word.WordDomain
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import com.san1ch.vocabanana.feature.text.domain.usecase.TextProcessingService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GenerateWordsFromTextUseCaseTest {

    private val textRepository: TextRepository = mockk()
    private val tps: TextProcessingService = mockk()
    private val wordRepository: WordRepository = mockk()
    private val generateService: GenerateWordsFromTextService = mockk()

    private lateinit var useCase: GenerateWordsFromTextUseCase

    @BeforeEach
    fun setUp() {
        useCase = GenerateWordsFromTextUseCase(
            textRepository = textRepository,
            tps = tps,
            wordRepository = wordRepository,
            generateService = generateService
        )
    }

    @Test
    fun `invoke should emit TextNotFound error state when text repository fails`() = runBlocking {
        val textId = 1
        coEvery { textRepository.getTextById(textId) } returns Result.failure(Exception("Not found"))

        val states = useCase(textId).toList()

        assertEquals(2, states.size)
        assertEquals(GenerateWordsFromTextState.Loading.PreparingText, states[0])
        assertEquals(
            GenerateWordsFromTextState.Error(GenerateWordsFromTextResult.Error.TextNotFound),
            states[1]
        )
    }

    @Test
    fun `invoke should emit AllWordsAlreadyExists when filtering returns empty list`() = runBlocking {
        val textId = 1
        val rawText = "Hello world"
        val mockTextDomain = TextDomain.unsafeCreate(
            id = textId,
            name = "Title",
            content = rawText,
            lastScrollPosition = 0f,
            lastReadTime = 0L
        )
        val frequencies = mapOf("hello" to 1, "world" to 1)

        coEvery { textRepository.getTextById(textId) } returns Result.success(mockTextDomain)
        every { tps.prepareText(rawText) } returns frequencies
        coEvery { generateService.filterByUserVocab(listOf("hello", "world")) } returns emptyList()

        val states = useCase(textId).toList()

        assertEquals(3, states.size)
        assertEquals(GenerateWordsFromTextState.Loading.PreparingText, states[0])
        assertEquals(GenerateWordsFromTextState.Loading.AnalyzingLexicon, states[1])
        assertEquals(
            GenerateWordsFromTextState.Success(GenerateWordsFromTextResult.Success.AllWordsAlreadyExists),
            states[2]
        )
    }

    @Test
    fun `invoke should successfully save words and emit Success state`() = runBlocking {
        val textId = 1
        val rawText = "Learn Kotlin"
        val mockTextDomain = TextDomain.unsafeCreate(
            id = textId,
            name = "Title",
            content = rawText,
            lastScrollPosition = 0f,
            lastReadTime = 0L
        )
        val frequencies = mapOf("learn" to 1, "kotlin" to 1)
        val wordsToProcess = listOf("learn", "kotlin")

        val mockWordDomain1 = WordDomain.createUnsafe(
            id = 1,
            lemma = "learn",
            whenAdded = System.currentTimeMillis(),
            state = WordState.NEW,
            forms = emptyList(),
            partOfSpeech = PartOfSpeech.VERB,
            definition = "to acquire knowledge"
        )
        val mockWordDomain2 = WordDomain.createUnsafe(
            id = 2,
            lemma = "kotlin",
            whenAdded = System.currentTimeMillis(),
            state = WordState.NEW,
            forms = emptyList(),
            partOfSpeech = PartOfSpeech.NOUN,
            definition = "programming language"
        )
        val domainsToAdd = listOf(mockWordDomain1, mockWordDomain2)

        coEvery { textRepository.getTextById(textId) } returns Result.success(mockTextDomain)
        every { tps.prepareText(rawText) } returns frequencies
        coEvery { generateService.filterByUserVocab(wordsToProcess) } returns wordsToProcess
        coEvery { generateService.generateDomains(wordsToProcess) } returns domainsToAdd
        coEvery { wordRepository.addWords(domainsToAdd) } returns Unit

        val states = useCase(textId).toList()

        assertTrue(states.size >= 4)
        assertEquals(GenerateWordsFromTextState.Loading.PreparingText, states[0])
        assertEquals(GenerateWordsFromTextState.Loading.AnalyzingLexicon, states[1])
        assertEquals(GenerateWordsFromTextState.Loading.SavingWords, states[2])

        val finalState = states.last()
        assertTrue(finalState is GenerateWordsFromTextState.Success)
        val result = (finalState as GenerateWordsFromTextState.Success).result
        assertTrue(result is GenerateWordsFromTextResult.Success.Words)
        assertEquals(domainsToAdd, (result as GenerateWordsFromTextResult.Success.Words).words)

        coVerify(exactly = 1) { wordRepository.addWords(domainsToAdd) }
    }
}