package com.san1ch.vocabanana.feature.debug.domain

import com.san1ch.vocabanana.core.essentials.model.word.WordQuery

interface DebugAssistant {

    suspend fun printAllWords()

    suspend fun printWords(query: WordQuery)

    suspend fun printWordCounts(
        title: String,
        query: WordQuery
    )
}