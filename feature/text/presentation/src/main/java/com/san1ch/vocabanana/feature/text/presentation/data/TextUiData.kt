package com.san1ch.vocabanana.feature.text.presentation.data

import com.san1ch.vocabanana.core.essentials.database.model.text.TextDomain



sealed class TextToken {
    data class Word(val text: String) : TextToken()
    data class Symbol(val text: String) : TextToken()
}


fun String.tokenize(): List<TextToken> {
    val wordPattern = Regex("[a-zA-Z']+")

    // We split by anything that IS NOT a letter or an apostrophe
    // The delimiters (spaces, commas, dots) are kept in the list
    return this.split(Regex("(?<=[^a-zA-Z'])|(?=[^a-zA-Z'])"))
        .filter { it.isNotEmpty() }
        .map {
            if (it.matches(wordPattern)) TextToken.Word(it)
            else TextToken.Symbol(it)
        }
}