package com.san1ch.vocabanana.core.essentials.usecases

import com.san1ch.vocabanana.core.essentials.model.text.WordWithCount
import com.san1ch.vocabanana.core.essentials.model.word.FilterType
import com.san1ch.vocabanana.core.essentials.model.word.WordDomain
import com.san1ch.vocabanana.core.essentials.model.word.WordQuery
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetWordsUseCase
@Inject
constructor(
    private val wordRepository: WordRepository,
    private val textRepository: TextRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(query: WordQuery = WordQuery()): Flow<List<WordDomain>> = when (val textIds = query.textIds) {
        is FilterType.All -> {
            wordRepository.getWords(query.wordIds, query.states)
        }

        else -> {
            textRepository
                .getWordIdsByTextIds(textIds)
                .flatMapLatest { textWordIds ->
                    val combinedIds = intersectIds(textWordIds, query.wordIds)
                    wordRepository.getWords(combinedIds, query.states)
                }
        }
    }

    private fun intersectIds(
        idsFromText: List<Int>,
        wordIdsFilter: FilterType<Int>,
    ): FilterType<Int> = when (wordIdsFilter) {
        is FilterType.All -> FilterType.Include(idsFromText)
        is FilterType.Include -> FilterType.Include(idsFromText.filter { it in wordIdsFilter.items })
        is FilterType.Exclude -> FilterType.Include(idsFromText.filter { it !in wordIdsFilter.items })
    }
}

class GetWordsWithCountUseCase
@Inject
constructor(
    private val getWords: GetWordsUseCase,
    private val textRepository: TextRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        query: WordQuery = WordQuery(),
    ): Flow<List<WordWithCount>> = getWords(query).flatMapLatest { words ->
        val wordIds = words.map { it.id }

        val countsMap = textRepository.getTextWordCounts(wordIds)

        flowOf(
            words.map { word ->
                WordWithCount(
                    word = word,
                    count = countsMap[word.id] ?: 0,
                )
            },
        )
    }
}
