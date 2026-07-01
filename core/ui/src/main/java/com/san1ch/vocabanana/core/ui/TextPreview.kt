package com.san1ch.vocabanana.core.ui

import com.san1ch.vocabanana.core.essentials.model.text.TextDomain

data class TextPreview(
    val id: Int,
    val title: String,
    val lastReadTime: Long
)

data class TextUi(
    val id: Int,
    val title: String,
    val paragraphs: List<String>,
    val lastScrollPosition: Float,
    val lastReadTime: Long
)


fun TextDomain.toUi(): TextUi = TextUi(
    id = id,
    title = name,
    paragraphs = content.split("\n").filter { it.isNotBlank() }.map { it },
    lastScrollPosition = this.info.lastScrollPosition,
    lastReadTime = this.info.lastReadTime
)

fun TextDomain.toPreview(): TextPreview = TextPreview(
    id = id,
    title = name,
    lastReadTime = this.info.lastReadTime
)
