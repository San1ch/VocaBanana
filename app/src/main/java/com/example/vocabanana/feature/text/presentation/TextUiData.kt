package com.example.vocabanana.feature.text.presentation

import com.example.vocabanana.feature.text.data.TextDomain

data class TextPreview(
    val id: Int,
    val title: String
)
data class TextUi(
    val id: Int,
    val title: String,
    val content: String
)

fun TextDomain.toPreview(): TextPreview = TextPreview(
    id = id,
    title = name,
)

fun TextDomain.toUi(): TextUi = TextUi(
    id = id,
    title = name,
    content = content,
)
