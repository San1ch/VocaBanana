package com.san1ch.vocabanana.feature.text.domain.usecase

import com.san1ch.vocabanana.core.essentials.model.text.TextPreview
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.feature.text.domain.ReadingStateRepository
import com.san1ch.vocabanana.feature.text.domain.model.TextListPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTextPreviewsUseCase @Inject constructor(
    private val readingStateRepository: ReadingStateRepository,
    private val textRepository: TextRepository,
) {
    operator fun invoke(): Flow<List<TextListPreview>> = combine(
        textRepository.getTextsMetadata(),
        readingStateRepository.getAllReadingStateFlow,
    ) { allMetadata, readingStates ->

        val stateMap = readingStates.associateBy { it.textId }

        allMetadata.map { meta ->
            val state = stateMap[meta.id]
            TextListPreview(
                textPreview = TextPreview(meta.id, meta.name),
                lastReadTime = state?.lastReadTime ?: 0L,
            )
        }
    }
}
