package com.san1ch.vocabanana.feature.debug.presentation

import android.util.Log
import com.san1ch.vocabanana.core.essentials.model.word.WordQuery
import com.san1ch.vocabanana.core.essentials.usecases.GetWordsUseCase
import com.san1ch.vocabanana.core.essentials.usecases.GetWordsWithCountUseCase
import com.san1ch.vocabanana.feature.debug.domain.DebugAssistant
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DebugAssistantImpl
@Inject
constructor(
    private val getWords: GetWordsUseCase,
    private val getWordsWithCount: GetWordsWithCountUseCase,
) : DebugAssistant {
    override suspend fun printAllWords() {
        printWords(WordQuery())
    }

    override suspend fun printWords(query: WordQuery) {
        val words = getWords(query).first()

        Log.d(
            "DebugWords",
            buildString {
                appendLine("===== WORDS =====")

                words.forEach {
                    appendLine(
                        "#${it.id} ${it.lemma}  state=${it.state}",
                    )
                }

                appendLine("=================")
            },
        )
    }

    override suspend fun printWordCounts(
        title: String,
        query: WordQuery,
    ) {
        val words = getWordsWithCount(query).first()

        Log.d(
            "DebugWords",
            buildString {
                appendLine("===== ${title.uppercase()} COUNTS =====")

                words.forEach {
                    appendLine(
                        "#${it.word.id} ${it.word.lemma}  count=${it.count}",
                    )
                }

                appendLine("=======================")
            },
        )
    }
}
