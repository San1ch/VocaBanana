package com.example.vocabanana.feature.text.presentation.data

import com.example.vocabanana.feature.text.domain.model.TextDomain

data class TextPreview(
    val id: Int,
    val title: String,
    val lastReadTime: Long
)

data class TextUi(
    val id: Int,
    val title: String,
    val paragraphs: List<ParagraphUi>,
    val lastScrollPosition: Float,
    val lastReadTime: Long
)

fun TextDomain.toPreview(): TextPreview = TextPreview(
    id = id,
    title = name,
    lastReadTime = this.info.lastReadTime
)

fun TextDomain.toUi(): TextUi = TextUi(
    id = id,
    title = name,
    paragraphs = content.split("\n").filter { it.isNotBlank() }.map { ParagraphUi(rawText = it) },
    lastScrollPosition = this.info.lastScrollPosition,
    lastReadTime = this.info.lastReadTime
)

sealed class TextToken {
    data class Word(val text: String) : TextToken()
    data class Symbol(val text: String) : TextToken()
}

data class ParagraphUi(
    val rawText: String,
)

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