package com.san1ch.vocabanana.core.essentials.usecases

import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import com.san1ch.vocabanana.core.essentials.model.word.PartOfSpeech
import com.san1ch.vocabanana.core.essentials.model.word.WordDomain
import com.san1ch.vocabanana.core.essentials.model.word.WordQuery
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetWordsUseCaseTest {

    private val wordRepository = mockk<WordRepository>()
    private val textRepository = mockk<TextRepository>()

    private lateinit var useCase: GetWordsUseCase

    @BeforeEach
    fun setup() {
        useCase = GetWordsUseCase(
            wordRepository,
            textRepository
        )
    }

    @Test
    fun `should bypass text repository when text filter is All`() = runTest {
        val query = WordQuery()

        every {
            wordRepository.getWords(
                query.wordIds,
                query.states
            )
        } returns flowOf(emptyList())

        useCase(query).first()

        verify(exactly = 1) {
            wordRepository.getWords(query.wordIds, query.states)
        }

        verify(exactly = 0) {
            textRepository.getWordIdsByTextIds(any())
        }
    }

    @Test
    fun `should combine text ids with word ids`() = runTest {

        val query = WordQuery(
            textIds = FilterType.Include(listOf(1)),
            wordIds = FilterType.Include(listOf(2, 3, 4))
        )

        every {
            textRepository.getWordIdsByTextIds(query.textIds)
        } returns flowOf(listOf(3, 4, 5))

        every {
            wordRepository.getWords(
                FilterType.Include(listOf(3, 4)),
                query.states
            )
        } returns flowOf(emptyList())

        useCase(query).first()

        verify {
            wordRepository.getWords(
                FilterType.Include(listOf(3, 4)),
                query.states
            )
        }
    }

    @Test
    fun `should exclude ids correctly`() = runTest {

        val query = WordQuery(
            textIds = FilterType.Include(listOf(1)),
            wordIds = FilterType.Exclude(listOf(2, 5))
        )

        every {
            textRepository.getWordIdsByTextIds(query.textIds)
        } returns flowOf(listOf(1, 2, 3, 4, 5))

        every {
            wordRepository.getWords(
                FilterType.Include(listOf(1, 3, 4)),
                query.states
            )
        } returns flowOf(emptyList())

        useCase(query).first()

        verify {
            wordRepository.getWords(
                FilterType.Include(listOf(1, 3, 4)),
                query.states
            )
        }
    }
}

class GetWordsWithCountUseCaseTest {

    private val getWords = mockk<GetWordsUseCase>()
    private val textRepository = mockk<TextRepository>()

    private lateinit var useCase: GetWordsWithCountUseCase

    @BeforeEach
    fun setup() {
        useCase = GetWordsWithCountUseCase(
            getWords,
            textRepository
        )
    }

    @Test
    fun `should attach counts to words`() = runTest {

        val words = listOf(
            WordDomain.createUnsafe(
                id = 1,
                lemma = "apple",
                whenAdded = 0,
                state = WordState.NEW,
                forms = emptyList(),
                partOfSpeech = PartOfSpeech.NOUN,
                definition = ""
            ),
            WordDomain.createUnsafe(
                id = 2,
                lemma = "dog",
                whenAdded = 0,
                state = WordState.LEARNING,
                forms = emptyList(),
                partOfSpeech = PartOfSpeech.NOUN,
                definition = ""
            )
        )

        every {
            getWords(any())
        } returns flowOf(words)

        coEvery {
            textRepository.getTextWordCounts(listOf(1, 2))
        } returns mapOf(
            1 to 5,
            2 to 10
        )

        val result = useCase(WordQuery()).first()

        assertEquals(2, result.size)

        assertEquals(5, result[0].count)
        assertEquals(10, result[1].count)
    }

    @Test
    fun `should return zero when count is missing`() = runTest {

        val word = WordDomain.createUnsafe(
            id = 1,
            lemma = "apple",
            whenAdded = 0,
            state = WordState.NEW,
            forms = emptyList(),
            partOfSpeech = PartOfSpeech.NOUN,
            definition = ""
        )

        every {
            getWords(any())
        } returns flowOf(listOf(word))

        coEvery {
            textRepository.getTextWordCounts(listOf(1))
        } returns emptyMap()

        val result = useCase(WordQuery()).first()

        assertEquals(0, result.first().count)
    }
}
