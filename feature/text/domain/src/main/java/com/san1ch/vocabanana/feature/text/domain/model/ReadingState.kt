package com.san1ch.vocabanana.feature.text.domain.model

import com.san1ch.vocabanana.core.essentials.model.word.WordState

data class ReadingState(
    val textId: Int,

    val lastReadTime: Long? = null,
    val lastScrollPosition: Float? = null,

    val fontSize: Int = 16,
    val lineSpacing: Int = 20,
    val paragraphSpacing: Int = 16,
    val horizontalPadding: Int = 16,

    val activeWordStates: Set<WordState> = emptySet(),
)
