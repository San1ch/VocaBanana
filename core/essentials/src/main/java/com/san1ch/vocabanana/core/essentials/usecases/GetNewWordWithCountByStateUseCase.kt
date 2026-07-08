package com.san1ch.vocabanana.core.essentials.usecases

import com.san1ch.vocabanana.core.essentials.model.text.WordWithCount
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.core.essentials.repositories.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNewWordWithCountByStateUseCase @Inject constructor(
    private val wordRepository: WordRepository,
    private val textRepository: TextRepository
) {
    operator fun invoke(states: List<WordState>): Flow<List<WordWithCount>> {
        return wordRepository.getWordsByStates(states).map { globalWords ->
            val wordIds = globalWords.map { it.id }
            val allCountsForText = textRepository.getTextWordCounts(wordIds)
            val wordWithCount = globalWords.map { word ->
                WordWithCount(word, allCountsForText[word.id] ?: 0)
            }
            wordWithCount
        }
    }
}