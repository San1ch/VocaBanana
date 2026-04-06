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
    val content: String,
    val lastScrollPosition: Float,
    val lastReadTime: Long
)

fun TextDomain.toPreview(): TextPreview = TextPreview(
    id = id,
    title = name,
    lastReadTime = lastReadTime
)

fun TextDomain.toUi(): TextUi = TextUi(
    id = id,
    title = name,
    content = content,
    lastScrollPosition = lastScrollPosition,
    lastReadTime = lastReadTime
)
