package com.san1ch.vocabanana.feature.text.presentation.model

import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.feature.text.domain.model.TextListItem
import com.san1ch.vocabanana.feature.text.presentation.data.TextToken

data class TextWithContent(
    val text: TextListItem,
    val content: List<List<TextToken>>,
) {
    val id: Int = text.id
    val title: String = text.title
    val lastScrollPosition: Float? = text.lastScrollPosition
    val lastReadTime: Long? = text.lastReadTime
    val activeWordStates: Set<WordState> = text.activeWordStates
    val textAppearanceSettings: TextAppearanceSettings = text.textAppearanceSettings
}
