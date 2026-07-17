package com.san1ch.vocabanana.feature.text.domain.model

import com.san1ch.vocabanana.core.essentials.model.TextAppearanceSettings
import com.san1ch.vocabanana.core.essentials.model.word.WordState

data class TextListItem(
    val id: Int,

    val title: String,

    val lastReadTime: Long?,
    val lastScrollPosition: Float?,

    val textAppearanceSettings: TextAppearanceSettings,

    val activeWordStates: Set<WordState>,
)
