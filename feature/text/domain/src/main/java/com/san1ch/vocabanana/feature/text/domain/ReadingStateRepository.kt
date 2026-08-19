package com.san1ch.vocabanana.feature.text.domain

import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.feature.text.domain.model.ReadingState
import kotlinx.coroutines.flow.Flow

interface ReadingStateRepository {
    fun getReadingStateByIdFlow(id: Int): Flow<ReadingState>
    val getAllReadingStateFlow: Flow<List<ReadingState>>

    suspend fun updateProgress(
        textId: Int,
        lastReadTime: Long,
        scrollPosition: Float,
    )

    suspend fun updateAppearance(
        textId: Int,
        appearance: TextAppearanceSettings,
    )

    suspend fun updateWordStates(
        textId: Int,
        wordStates: Set<WordState>,
    )
}
