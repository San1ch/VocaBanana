package com.san1ch.vocabanana.feature.text.presentation.data

import androidx.compose.ui.graphics.Color
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.ui.theme.DarkReadingColor
import com.san1ch.vocabanana.core.ui.theme.LightReadingColor

sealed class TextToken {
    data class Word(val text: String, val state: WordState? = null) : TextToken()
    data class Symbol(val text: String) : TextToken()
}

fun String.tokenize(): List<TextToken> {
    val wordPattern = Regex("[a-zA-Z']+")

    // We split by anything that IS NOT a letter or an apostrophe
    // The delimiters (spaces, commas, dots) are kept in the list
    return this.split(Regex("(?<=[^a-zA-Z'])|(?=[^a-zA-Z'])"))
        .filter { it.isNotEmpty() }
        .map {
            if (it.matches(wordPattern)) {
                TextToken.Word(it)
            } else {
                TextToken.Symbol(it)
            }
        }
}
fun WordState?.toReadingStateColor(isLightTheme: Boolean, nullColor: Color): Color {
    val readingColor = if (isLightTheme) LightReadingColor else DarkReadingColor
    return when (this) {
        WordState.NEW -> readingColor.new
        WordState.LEARNING -> readingColor.learning
        WordState.NOT_KNOWN -> readingColor.notKnown
        WordState.KNOWN -> readingColor.known
        WordState.IGNORED -> readingColor.ignored
        null -> nullColor
    }
}
