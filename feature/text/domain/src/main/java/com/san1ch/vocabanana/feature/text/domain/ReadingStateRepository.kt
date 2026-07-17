package com.san1ch.vocabanana.feature.text.domain

import com.san1ch.vocabanana.feature.text.domain.model.ReadingState
import kotlinx.coroutines.flow.Flow

interface ReadingStateRepository {
    fun getReadingStateByIdFlow(id: Int): Flow<ReadingState>
    val getAllReadingStateFlow: Flow<List<ReadingState>>

    suspend fun setReadingState(readingState: ReadingState)
    suspend fun updateReadingState(
        id: Int,
        transform: (ReadingState) -> ReadingState,
    )
}
