package com.san1ch.vocabanana.core.essentials.extentionfuncs

fun String.toParagraphs(): List<String> {
    val paragraphs = this.split(Regex("\\n\\s*\\n"))
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    return if (paragraphs.size < 5 && this.contains("\n")) {
        this.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
    } else {
        paragraphs
    }
}
