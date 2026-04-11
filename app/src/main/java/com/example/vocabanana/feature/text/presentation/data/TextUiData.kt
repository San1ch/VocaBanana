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

data class ParagraphUi(
    val rawText: String,

    // for the future TODO
    // val words: List<WordEntity> 
)