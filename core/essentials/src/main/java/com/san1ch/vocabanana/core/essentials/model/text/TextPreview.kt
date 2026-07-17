package com.san1ch.vocabanana.core.essentials.model.text

data class TextPreview(
    val id: Int,
    val name: String,
)

fun TextDomain.toBasicPreview(): TextPreview = TextPreview(id, name)
