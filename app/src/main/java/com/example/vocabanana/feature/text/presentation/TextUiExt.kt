package com.example.vocabanana.feature.text.presentation

import com.example.vocabanana.feature.text.data.TextDomain

fun TextDomain.toPreview(): TextPreview = TextPreview(
    id = id,
    title = name,
)
fun TextDomain.toUi(): TextUi = TextUi(
    id = id,
    title = name,
    content = content,
)