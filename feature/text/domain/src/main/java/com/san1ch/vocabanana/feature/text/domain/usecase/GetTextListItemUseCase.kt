package com.san1ch.vocabanana.feature.text.domain.usecase

import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import com.san1ch.vocabanana.feature.text.domain.ReadingStateRepository
import com.san1ch.vocabanana.feature.text.domain.model.TextListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTextListItemUseCase @Inject constructor(
    private val textRepository: TextRepository,
    private val readingStateRepository: ReadingStateRepository
) {
    operator fun invoke(textId: Int): Flow<TextListItem> {
        val metadataFlow = textRepository.getTextMetadataByIdFlow(textId)
        val readingStateFlow = readingStateRepository.getReadingStateByIdFlow(textId)

        return combine(metadataFlow, readingStateFlow) { metadata, state ->
            val title = metadata?.name ?: "Unknown"

            TextListItem(
                id = textId,
                title = title,
                lastReadTime = state.lastReadTime,
                lastScrollPosition = state.lastScrollPosition,
                textAppearanceSettings = TextAppearanceSettings(
                    fontSize = state.fontSize,
                    lineSpacing = state.lineSpacing,
                    paragraphSpacing = state.paragraphSpacing,
                    horizontalPadding = state.horizontalPadding
                ),
                activeWordStates = state.activeWordStates
            )
        }.distinctUntilChanged()
    }
}

